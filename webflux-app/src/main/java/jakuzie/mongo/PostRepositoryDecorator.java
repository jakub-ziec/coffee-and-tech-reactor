package jakuzie.mongo;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostRepositoryDecorator implements PostRepository {

  private final PostMongoRepository delegate;

  @Setter
  @Getter
  private AtomicInteger findByIdCounter = null;

  @Override
  public Mono<Post> findById(UUID id) {
    return delegate.findById(id)
        .doOnSuccess(post -> {
          if (findByIdCounter != null) {
              findByIdCounter.incrementAndGet();
          }
        })
        .doOnError(throwable -> {
          if (findByIdCounter != null) {
            findByIdCounter.incrementAndGet();
          }
        });
  }

  @Override
  public Flux<Post> findAll() {
    return delegate.findAll();
  }

  @Override
  public Mono<Post> save(Post post) {
    return delegate.save(post);
  }
}
