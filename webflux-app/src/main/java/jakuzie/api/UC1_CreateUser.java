package jakuzie.api;

import jakuzie.exception.InvalidEmailException;
import jakuzie.mongo.User;
import jakuzie.mongo.UserRepository;
import jakuzie.rest.AvatarClient;
import jakuzie.rest.EmailClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UC1_CreateUser {

  private final UserRepository userRepository;
  private final EmailClient emailClient;
  private final AvatarClient avatarClient;

  @PostMapping("/users")
  public Mono<User> createUser(@RequestBody CreateUserRequest request) {
    return Mono.zip(
            emailClient.isEmailValid(request.email()),
            avatarClient.getRandomAvatarUrl())
        .flatMap(tuple -> tuple.getT1()
            ? Mono.just(new User(request.email(), tuple.getT2()))
            : Mono.error(new InvalidEmailException(request.email())))
        .flatMap(userRepository::save);
  }

  public record CreateUserRequest(String email) {

  }

}
