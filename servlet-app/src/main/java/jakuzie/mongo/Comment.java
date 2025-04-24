package jakuzie.mongo;

import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@Document("comments")
public class Comment {

  @Id
  private UUID id;
  private UUID postId;
  private String content;
  private String authorName;
  private Instant createdAt;

  public Comment(UUID postId, String content, String authorName) {
    this(
        UUID.randomUUID(),
        postId,
        content,
        authorName,
        Instant.now());
  }

}
