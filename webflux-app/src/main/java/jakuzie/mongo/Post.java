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
@Document("posts")
public class Post {

  @Id
  private UUID id;
  private String title;
  private String content;
  private String authorName;
  private Instant createdAt;

  public Post(String title, String content, String authorName) {
    this(
        UUID.randomUUID(),
        title,
        content,
        authorName,
        Instant.now());
  }

  public Post(UUID id, String title, String content, String authorName) {
    this(
        id,
        title,
        content,
        authorName,
        Instant.now());
  }

}
