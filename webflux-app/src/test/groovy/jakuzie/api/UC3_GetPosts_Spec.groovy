package jakuzie.api

import jakuzie.fixtures.BaseSpringTest
import jakuzie.mongo.Post

import static org.hamcrest.Matchers.equalTo

class UC3_GetPosts_Spec extends BaseSpringTest {

    def "returns 200 on get posts request"() {
        given:
        def post = new Post("title", "content", "author")
        postRepository.save(post).block()
        def postId = post.getId().toString()

        when:
        def res = requestSpec()
                .auth().basic("user", "user")
                .get('/posts')
                .then()

        then:
        res.statusCode(200)
                .body("[0].id", equalTo(postId))
                .body("[0].title", equalTo(post.getTitle()))
                .body("[0].content", equalTo(post.getContent()))
                .body("[0].author", equalTo(post.getAuthorName()))
    }

}
