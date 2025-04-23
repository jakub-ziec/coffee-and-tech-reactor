package jakuzie.mongo;

import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, UUID> {

  List<Comment> findByPostId(UUID postId);

}
