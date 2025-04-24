package jakuzie.fixtures

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import groovy.transform.TupleConstructor

import java.time.Duration

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

@TupleConstructor
class FakeRemoteService {

    WireMockServer wireMockServer
    ObjectMapper objectMapper

    def stubIsEmailValidRespondsOk(String email, boolean valid, int delay = 0) {
        wireMockServer.stubFor(
                post(urlEqualTo("/validate-email"))
                        .withRequestBody(equalToJson("{\"email\":\"$email\"}"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(asJsonString([
                                        "valid": valid
                                ]))
                                .withFixedDelay(delay)
                        )
        )
        this
    }

    def stubGetRandomAvatarUrlRespondsOk(String avatarUrl = "https://placehold.co/400", int delay = 0) {
        wireMockServer.stubFor(
                get(urlEqualTo("/random-avatar"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(asJsonString([
                                        avatarUrl: avatarUrl
                                ]))
                                .withFixedDelay(delay)
                        )
        )
        this
    }

    def stubIsCommentSafeRespondsOk(boolean safe) {
        wireMockServer.stubFor(
                post(urlEqualTo("/safety-check"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(asJsonString([
                                        "safe": safe
                                ]))
                        )
        )
        this
    }

    def stubIsAuthorUnlockedRespondsOk(boolean unlocked) {
        wireMockServer.stubFor(
                post(urlEqualTo("/author-check"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(asJsonString([
                                        "unlocked": unlocked
                                ]))
                        )
        )
        this
    }

    def setGlobalFixedDelay(Duration delay) {
        wireMockServer.setGlobalFixedDelay(delay.toMillis().intValue())
        this
    }

    void verifyIsEmailValidCalled(int count) {
        wireMockServer.verify(count,
                postRequestedFor(urlEqualTo("/validate-email"))
        )
    }

    private String asJsonString(Object obj) {
        return objectMapper.writeValueAsString(obj)
    }

}
