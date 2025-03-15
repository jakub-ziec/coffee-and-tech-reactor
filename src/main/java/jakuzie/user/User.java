package jakuzie.user;

import static lombok.AccessLevel.PRIVATE;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class User {
  private UUID id;
  private String email;
  private String avatarUrl;

  public static User newUser(String email, String avatarUrl) {
    return new User(UUID.randomUUID(), email, avatarUrl);
  }

}
