package jakuzie.mongo;

import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, UUID> {

}
