package jakuzie

import jakuzie.fixtures.TestMono
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class OrchestratorSpec extends Specification {

    Orchestrator orchestrator

    AaaService aaaService = Mock()
    BbbService bbbService = Mock()

    void setup() {
        orchestrator = new Orchestrator(aaaService, bbbService)
    }

    def "should not call bbbService when aaaService returns error"() {
        when:
        StepVerifier.create(orchestrator.orchestrate())
                .verifyError()

        then:
        1 * aaaService.doSth() >> Mono.error(new RuntimeException())
        0 * bbbService.doSth() // Too many invocations
    }

    def "should not call bbbService when aaaService returns error - using TestMono"() {
        given:
        def bbbRes = TestMono.empty()

        when:
        StepVerifier.create(orchestrator.orchestrate())
                .verifyError()

        then:
        1 * aaaService.doSth() >> Mono.error(new RuntimeException())
        1 * bbbService.doSth() >> bbbRes
        bbbRes.assertSubscribeCount(0)
    }

    def "should call bbbService when aaaService returns error, then retries"() {
        given:
        def aaaRes = TestMono.fromMonos([Mono.error(new RuntimeException()), Mono.just("A")])
        def bbbRes = TestMono.just("B")

        when:
        StepVerifier.create(orchestrator.orchestrateWithRetry())
                .expectNext("B")
                .verifyComplete()

        then:
        1 * aaaService.doSth() >> aaaRes
        1 * bbbService.doSth() >> bbbRes
        aaaRes.assertSubscribeCount(2)
        bbbRes.assertSubscribeCount(1)
    }

}
