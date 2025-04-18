package jakuzie.api;

import jakuzie.email.EmailSender;
import jakuzie.email.EmailSender.WelcomeEmail;
import jakuzie.exception.InvalidEmailException;
import jakuzie.mongo.User;
import jakuzie.mongo.UserRepository;
import jakuzie.rabbit.EventPublisher;
import jakuzie.rabbit.EventPublisher.UserCreated;
import jakuzie.rest.AvatarClient;
import jakuzie.rest.EmailValidatorClient;
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
  private final EmailValidatorClient emailValidatorClient;
  private final AvatarClient avatarClient;
  private final EmailSender emailSender;
  private final EventPublisher eventPublisher;

  @PostMapping("/users")
  public Mono<User> createUser(@RequestBody CreateUserRequest request) {
    return Mono.zip(
            emailValidatorClient.isEmailValid(request.email()),
            avatarClient.getRandomAvatarUrl())
        .flatMap(tuple -> tuple.getT1()
            ? Mono.just(new User(request.email(), tuple.getT2()))
            : Mono.error(new InvalidEmailException(request.email())))
        .flatMap(userRepository::save)
        .delayUntil(user -> Mono.zip(
            eventPublisher.publish(new UserCreated(user))
                .thenReturn(1),
            emailSender.sendEmail(new WelcomeEmail(user))
        ));
  }

  public record CreateUserRequest(String email) {

  }

}
