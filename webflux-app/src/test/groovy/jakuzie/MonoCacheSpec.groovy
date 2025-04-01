package jakuzie


import reactor.core.publisher.Mono
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicInteger

class MonoCacheSpec extends Specification {

    def 'should subscribe 3 times'() {
        given:
        def counter = new AtomicInteger(0)
        def mono = Mono.fromCallable {
            return counter.getAndIncrement()
        }
        .doOnSubscribe { println("doOnSubscribe()") }
        .doOnNext { println("doOnNext($it)") }

        when:
        def res = [mono, mono, mono].collect { it.block() }

        then:
        res == [0, 1, 2]
    }

    def 'should subscribe once'() {
        given:
        def counter = new AtomicInteger(0)
        def mono = Mono.fromCallable {
            return counter.getAndIncrement()
        }
                .doOnSubscribe { println("doOnSubscribe()") }
                .doOnNext { println("doOnNext($it)") }
                .cache()

        when:
        def res = [mono, mono, mono].collect { it.block() }

        then:
        res == [0, 0, 0]
    }


}
