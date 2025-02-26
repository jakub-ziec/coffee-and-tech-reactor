package jakuzie

import jakuzie.fixtures.TestMono
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class FooServiceSpec extends Specification {

    FooService fooService

    BarClient barClient

    void setup() {
        barClient = Mock()
        fooService = new FooService(barClient)
    }

    def "should call barClient"() {
        when:
        StepVerifier.create(fooService.doSth(false))
                .verifyComplete()

        then:
        0 * barClient.doSth() >> Mono.empty()
    }

    def "should call barClient - using TestMono"() {
        given:
        def barClientPublisher = TestMono.empty()

        when:
        StepVerifier.create(fooService.doSth(false))
                .verifyComplete()

        then:
        1 * barClient.doSth() >> barClientPublisher
        barClientPublisher.assertSubscribeCount(0)
    }

}
