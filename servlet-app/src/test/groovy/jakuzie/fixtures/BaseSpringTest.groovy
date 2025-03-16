package jakuzie.fixtures

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import io.restassured.RestAssured
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.springframework.boot.test.context.SpringBootTest
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
    protected RequestSpecification requestSpec

    @Shared
    private ObjectMapper objectMapper

//    @Shared
//    WireMockServer wireMockServer

    def setupSpec() {
//        wireMockServer = new WireMockServer(options().port(1_9999)
//                .notifier(new ConsoleNotifier(true)))
//        wireMockServer.start()
        requestSpec = RestAssured.given()
                .baseUri("http://localhost:${1_8080}")
                .filters(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                )
                .contentType(ContentType.JSON)
        objectMapper = new Jackson2ObjectMapperBuilder().build()
    }

    void setup() {
//        wireMockServer.resetAll()
//        fakeRemoteService = new FakeRemoteService(wireMockServer, objectMapper)
        RestAssured.reset()
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(ALL)
    }

    void cleanupSpec() {
//        wireMockServer.stop()
    }

}
