package jakuzie.api

import io.restassured.response.ValidatableResponse
import jakuzie.fixtures.BaseSpringTest
import jakuzie.fixtures.TestRabbitListener
import jakuzie.mongo.SendEmailOutboxRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Timeout

import java.util.concurrent.TimeUnit

import static java.time.Duration.ofSeconds
import static org.awaitility.Awaitility.await

class UC1_CreateUser_Spec extends BaseSpringTest {

    @Autowired
    TestRabbitListener testRabbitListener

    @Autowired
    SendEmailOutboxRepository emailOutboxRepository

    def "creates user"() {
        given:
        def email = "valid@example.com"
        fakeRemoteService
                .stubIsEmailValidRespondsOk(email, true)
                .stubGetRandomAvatarUrlRespondsOk()

        when:
        def res = makeRequestCreateUser(email)

        then:
        res.statusCode(200)
    }

    def "returns 400 Bad request when email is invalid"() {
        given:
        def email = "invalid@example.com"
        fakeRemoteService
                .stubIsEmailValidRespondsOk(email, false)
                .stubGetRandomAvatarUrlRespondsOk()


        when:
        def res = makeRequestCreateUser(email)

        then:
        res.statusCode(400)
    }

    @Timeout(value = 199, unit = TimeUnit.MILLISECONDS)
    def "makes calls to remote services in parallel"() {
        given:
        def email = "valid@example.com"
        fakeRemoteService
                .stubIsEmailValidRespondsOk(email, true, 100)
                .stubGetRandomAvatarUrlRespondsOk("https://placehold.co/400", 100)

        when:
        def res = makeRequestCreateUser(email)

        then:
        res.statusCode(200)
    }

    def "working cache - makes call to email validation service only once"() {
        given:
        def email = "valid@example.com"
        fakeRemoteService
                .stubIsEmailValidRespondsOk(email, true)
                .stubGetRandomAvatarUrlRespondsOk()

        when:
        def res1 = makeRequestCreateUser(email)
        def res2 = makeRequestCreateUser(email)

        then:
        res1.statusCode(200)
        res2.statusCode(200)
        fakeRemoteService.verifyIsEmailValidCalled(1)
    }

    def "sends event and welcome email"() {
        given:
        emailOutboxRepository.deleteAll().block()
        testRabbitListener.reset()
        def email = "valid@example.com"
        fakeRemoteService
                .stubIsEmailValidRespondsOk(email, true)
                .stubGetRandomAvatarUrlRespondsOk()
        emailSender.setDelay(100)

        when:
        def res = makeRequestCreateUser(email)
        await()
                .atMost(ofSeconds(1))
                .until { testRabbitListener.userCreatedEvents.poll() != null }

        then:
        res.statusCode(200)
        assertEmailSent()
    }

    private ValidatableResponse makeRequestCreateUser(String email) {
        return requestSpec()
                .body([
                        email: email
                ])
                .post('/users')
                .then()
    }

    private void assertEmailSent() {
        def emailSent = emailOutboxRepository.findAll().collectList().block().size() == 1
        assert emailSent: "Expected email to be sent, but it was not."
    }

}
