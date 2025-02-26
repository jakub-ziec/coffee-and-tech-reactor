package jakuzie.fixtures

import reactor.core.CoreSubscriber
import reactor.core.publisher.Mono

import java.util.concurrent.atomic.AtomicInteger

class TestMono<T> extends Mono<T> {

    private final AtomicInteger counter
    private final Mono<T> delegate

    private TestMono(AtomicInteger counter, Mono<T> delegate) {
        this.counter = counter
        this.delegate = delegate
    }

    static <T> TestMono<T> fromIterable(Iterable<T> values) {
        AtomicInteger counter = new AtomicInteger(0)
        if (values.isEmpty()) {
            return new TestMono<T>(counter, Mono.empty())
        }
        List<T> listOfValues = values.toList()
        return new TestMono<T>(counter, Mono.defer {
            def index = Math.min(counter.incrementAndGet(), values.size()) - 1
            return Mono.just(listOfValues[index])
        })
    }

    static <T> TestMono<T> just(T value) {
        return fromIterable([value])
    }

    static <T> TestMono<T> empty() {
        return fromIterable([] as List<T>)
    }

    @Override
    void subscribe(CoreSubscriber<? super T> actual) {
        delegate.subscribe(actual)
    }

    int getSubscribeCount() {
        return counter.get()
    }

    void assertSubscribeCount(int expected) {
        assert counter.get() == expected
    }
}
