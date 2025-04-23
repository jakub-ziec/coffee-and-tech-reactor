package jakuzie.api

import jakuzie.fixtures.BaseSpringTest
import jakuzie.mongo.Comment
import jakuzie.mongo.Post

import static org.hamcrest.Matchers.equalTo

class UC4_GetPostDetails_Spec extends BaseSpringTest {

    def "returns 200 on get post details request"() {
        given:
        def post = new Post("title", "content", "author1")
        postRepository.save(post).block()
        def comment = new Comment(post.getId(), "comment", "author2")
        commentRepository.save(comment).block()

        when:
        def res = requestSpec()
                .auth().basic("user", "user")
                .get('/posts/{postId}', post.getId().toString())
                .then()

        then:
        res.statusCode(200)
                .body("id", equalTo(post.getId().toString()))
                .body("title", equalTo(post.getTitle()))
                .body("content", equalTo(post.getContent()))
                .body("author", equalTo(post.getAuthorName()))
                .body("comments[0].content", equalTo(comment.getContent()))
                .body("comments[0].author", equalTo(comment.getAuthorName()))
    }

    def "returns 404 on get post details request for non-existing post"() {
        given:
        def postId = UUID.randomUUID()

        when:
        def res = requestSpec()
                .auth().basic("user", "user")
                .get('/posts/{postId}', postId.toString())
                .then()

        then:
        res.statusCode(404)
    }

}
