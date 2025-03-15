package jakuzie

import groovy.transform.TupleConstructor
import reactor.core.publisher.Mono
import spock.lang.Specification

class MonoZipSpec extends Specification {

    interface Foo {
        Mono<String> foo()
    }

    interface Bar {
        Mono<String> bar()
    }

    @TupleConstructor
    class Xyz {
        Foo foo
        Bar bar

        Mono<String> xyz() {
            return Mono.zip(foo.foo(), bar.bar())
                    .doOnNext { println("doOnNext $it") }
            .delayUntil {}
                    .map { it.getT1() + it.getT2() }
        }
    }

    def 'should zip'() {
        given:
        Foo foo = {
            println "foo()"
            Mono.empty()
        }
        Bar bar = {
            println "bar()"
            Mono.empty()
        }
        def xyz = new Xyz(foo, bar)

        when:
        def res = xyz.xyz().block()

        then:
        noExceptionThrown()
        res == null
    }


}
