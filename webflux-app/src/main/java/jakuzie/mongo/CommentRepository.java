package jakuzie.mongo;

import java.util.UUID;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CommentRepository extends ReactiveMongoRepository<Comment, UUID> {

  Flux<Comment> findByPostId(UUID postId);

}
