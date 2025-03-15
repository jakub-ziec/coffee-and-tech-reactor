package jakuzie.user.nonblocking;

import jakuzie.user.User;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class NonBlockingUserRepository {

  public Mono<User> save(User user) {
    return Mono.just(user);
  }

}
