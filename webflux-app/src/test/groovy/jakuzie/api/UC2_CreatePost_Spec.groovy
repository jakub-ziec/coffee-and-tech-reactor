package jakuzie.api


import jakuzie.fixtures.BaseSpringTest
import jakuzie.fixtures.TestRabbitListener
import jakuzie.rabbit.EventPublisher
import org.springframework.beans.factory.annotation.Autowired

import static jakuzie.rabbit.RabbitConfig.POST_CREATED_QUEUE

class UC2_CreatePost_Spec extends BaseSpringTest {

    @Autowired
    TestRabbitListener testRabbitListener

    @Autowired
    EventPublisher eventPublisher

    def "returns 200 on creates post request"() {
        given:
        def body = [
                title: "foo",
                content : "bar"
        ]

        when:
        def res = requestSpec()
                .body(body)
                .auth().basic("user", "user")
                .post('/posts')
                .then()

        then:
        res.statusCode(200)
    }

    def "returns 401 when no credentials provided on creates post request"() {
        given:
        def body = [
                title: "foo",
                content : "bar"
        ]

        when:
        def res = requestSpec()
                .body(body)
                .auth().none()
                .post('/posts')
                .then()

        then:
        res.statusCode(401)
    }

    def "tx check"() {
        given:
        def body = [
                title: "foo",
                content : "bar"
        ]
        and:
        eventPublisher.postCreatedQueue = null

        when:
        def res = requestSpec()
                .body(body)
                .auth().basic("user", "user")
                .post('/posts')
                .then()

        then:
        res.statusCode(500)

        and:
        postRepository.findAll().collectList().block() == []

        cleanup:
        eventPublisher.postCreatedQueue = POST_CREATED_QUEUE
    }

}
