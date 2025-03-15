package jakuzie

import groovy.transform.TupleConstructor
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

import java.time.Duration

class FluxOnErrorContinueSpec extends Specification {

    def 'should not cancel stream'() {
        when:
        def flux = Flux.interval(Duration.ofSeconds(1))
                .doOnEach { println("After interval() $it") }
                .doOnCancel { println("After interval() cancelled") }
                .handle { it, sink ->
                    if (it == 3) {
                        return sink.error(new RuntimeException("error"))
                    }
                    sink.next("#$it")
                }
//                .onErrorResume(e -> Mono.empty())
                .doOnEach { println("After handle() $it") }
                .doOnCancel { println("After handle() cancelled") }
                .map { it + "!" }
//                .onErrorResume(e -> Mono.empty())
                .onErrorContinue { e, o -> println("Continuing on error: $e, object: $o") }
                .doOnEach {println("After map() ${it.getType()}(${it.get()})") }
                .doOnCancel { println("After map() cancelled") }
                .take(10)

        def res = flux.collectList().block()

        then:
        res.size() == 10
    }


}
