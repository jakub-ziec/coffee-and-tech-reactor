package jakuzie.user.blocking;

import jakuzie.user.User;
import org.springframework.stereotype.Repository;

@Repository
public class BlockingUserRepository {

  public User save(User user) {
    return user;
  }

}
