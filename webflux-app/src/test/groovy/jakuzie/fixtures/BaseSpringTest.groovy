package jakuzie.fixtures

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import io.restassured.RestAssured
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import jakuzie.email.EmailSender
import jakuzie.mongo.CommentRepository
import jakuzie.mongo.PostMongoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import static io.restassured.filter.log.LogDetail.ALL

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(["test"])
abstract class BaseSpringTest extends Specification {

    protected FakeRemoteService fakeRemoteService

    @Shared
    private ObjectMapper objectMapper

    @Shared
    WireMockServer wireMockServer

    @Autowired
    CacheManager cacheManager

    @Autowired
    EmailSender emailSender

    @Autowired
    PostMongoRepository postRepository

    @Autowired
    CommentRepository commentRepository

    def setupSpec() {
//        WireMock.configureFor("localhost", 9999)

        wireMockServer = new WireMockServer(options().port(19999)
                .notifier(new ConsoleNotifier(true)))
        wireMockServer.start()
        objectMapper = new Jackson2ObjectMapperBuilder().build()
    }

    void setup() {
//        WireMock.setGlobalFixedDelay(0)
        wireMockServer.resetAll()
        wireMockServer.setGlobalFixedDelay(0)
        fakeRemoteService = new FakeRemoteService(wireMockServer, objectMapper)
        RestAssured.reset()
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(ALL)
        cacheManager.cacheNames
                .collect { cacheManager.getCache(it) }
                .each { it.invalidate() }
        emailSender.setDelay(0)
        postRepository.deleteAll().block()
        commentRepository.deleteAll().block()
    }

    void cleanupSpec() {
        wireMockServer.stop()
    }

    static def requestSpec() {
        RestAssured.given()
                .baseUri("http://localhost:${1_8081}")
                .filters(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                )
                .contentType(ContentType.JSON)
    }

}
