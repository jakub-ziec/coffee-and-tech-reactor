package jakuzie.mongo;

import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepository {

  Mono<Post> findById(UUID id);

  Flux<Post> findAll();

  Mono<Post> save(Post post);

  Flux<Post> findAllById(Iterable<UUID> postIds);

  Flux<Post> saveAll(Iterable<Post> posts);

  Mono<Void> deleteAll();
}
