package jakuzie.mongo;

import java.util.UUID;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, UUID> {
}
