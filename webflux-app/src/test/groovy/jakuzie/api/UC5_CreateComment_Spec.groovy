package jakuzie.api

import jakuzie.fixtures.BaseSpringTest
import jakuzie.mongo.Post
import jakuzie.mongo.PostRepositoryDecorator
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.util.concurrent.atomic.AtomicInteger

class UC5_CreateComment_Spec extends BaseSpringTest {

    @Autowired
    PostRepositoryDecorator postDelegateRepository

    def "returns 200 on create comment request"() {
        given:
        def post = new Post("title", "content", "joe")
        postRepository.save(post).block()
        def postId = post.getId().toString()
        def body = [
                comment: "comment"
        ]
        fakeRemoteService
                .stubIsCommentSafeRespondsOk(true)
                .stubIsAuthorUnlockedRespondsOk(true)

        when:
        def res = requestSpec()
                .body(body)
                .auth().basic("user", "user")
                .post('/posts/{postId}/comments', postId)
                .then()

        then:
        res.statusCode(200)
    }

    def "returns 404 on create comment request when post doesn't exists"() {
        given:
        def postId = UUID.randomUUID().toString()
        def body = [
                comment: "comment"
        ]

        when:
        def res = requestSpec()
                .body(body)
                .auth().basic("user", "user")
                .post('/posts/{postId}/comments', postId)
                .then()

        then:
        res.statusCode(404)
    }

    @Unroll
    def "returns 422 on create comment request"() {
        given:
        def post = new Post("title", "content", "joe")
        postRepository.save(post).block()
        def postId = post.getId().toString()
        def body = [
                comment: "comment"
        ]
        fakeRemoteService
                .stubIsCommentSafeRespondsOk(isCommentSafe)
                .stubIsAuthorUnlockedRespondsOk(isAuthorUnlocked)

        when:
        def res = requestSpec()
                .body(body)
                .auth().basic("user", "user")
                .post('/posts/{postId}/comments', postId)
                .then()

        then:
        res.statusCode(422)

        where:
        isCommentSafe | isAuthorUnlocked
        false         | true
        true          | false
        false         | false
    }

    def "invokes postRepository.findById only once"() {
        given:
        def post = new Post("title", "content", "joe")
        postRepository.save(post).block()
        def postId = post.getId().toString()
        def body = [
                comment: "comment"
        ]
        fakeRemoteService
                .stubIsCommentSafeRespondsOk(true)
                .stubIsAuthorUnlockedRespondsOk(true)
        and:
        def findByIdCounter = new AtomicInteger(0)
        postDelegateRepository.findByIdCounter = findByIdCounter

        when:
        def res = requestSpec()
                .body(body)
                .auth().basic("user", "user")
                .post('/posts/{postId}/comments', postId)
                .then()

        then:
        res.statusCode(200)
        findByIdCounter.get() == 1

        cleanup:
        postDelegateRepository.findByIdCounter = null
    }

}
