package jakuzie.mongo;

import static lombok.AccessLevel.PRIVATE;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@Document("users")
public class User {
  @Id
  private UUID id;
  private String email;
  private String avatarUrl;

  public User(String email, String avatarUrl) {
    this(UUID.randomUUID(), email, avatarUrl);
  }

}
