package jakuzie.api

import jakuzie.fixtures.BaseSpringTest

class UC1_CreateUser_Spec extends BaseSpringTest {

    def "creates user"() {
        given:
        def email = "valid@example.com"

        when:
        def res = requestSpec
                .body([
                        email: email
                ])
                .post('/users')
                .then()

        then:
        res.statusCode(200)
    }

    def "returns 400 Bad request when email is invalid"() {
        given:
        def email = "invalid@example.com"

        when:
        def res = requestSpec
                .body([
                        email: email
                ])
                .post('/users')
                .then()

        then:
        res.statusCode(400)
    }

}
