package jakuzie.fixtures

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import groovy.transform.TupleConstructor

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

@TupleConstructor
class FakeRemoteService {

    WireMockServer wireMockServer
    ObjectMapper objectMapper

    def stubIsEmailValidRespondsOk(String email, boolean valid) {
        wireMockServer.stubFor(
                post(urlEqualTo("/validate-email"))
                        .withRequestBody(equalToJson("{\"email\":\"$email\"}"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(asJsonString([
                                        "valid": valid
                                ]))
                        )
        )
        this
    }

    String asJsonString(Object obj) {
        return objectMapper.writeValueAsString(obj)
    }

    def stubGetRandomAvatarUrlRespondsOk(String avatarUrl) {
        wireMockServer.stubFor(
                get(urlEqualTo("/random-avatar"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(asJsonString([
                                        avatarUrl: avatarUrl
                                ]))
                        )
        )
    }
}
