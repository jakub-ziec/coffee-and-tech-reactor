package jakuzie.mongo;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("send-email-outbox")
public record SendEmailOutbox(
    @Id String id, String to, String subject, String body, Instant createdAt
) {

  public SendEmailOutbox(String to, String subject, String body) {
    this(UUID.randomUUID().toString(), to, subject, body, Instant.now());
  }

}
