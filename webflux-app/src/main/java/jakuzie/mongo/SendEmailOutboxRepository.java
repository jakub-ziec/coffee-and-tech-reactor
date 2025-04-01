package jakuzie.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface SendEmailOutboxRepository extends ReactiveMongoRepository<SendEmailOutbox, String> {
}
