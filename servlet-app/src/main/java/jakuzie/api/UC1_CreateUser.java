package jakuzie.api;

import jakuzie.exception.InvalidEmailException;
import jakuzie.mongo.User;
import jakuzie.mongo.UserRepository;
import jakuzie.rest.AvatarClient;
import jakuzie.rest.EmailValidatorClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UC1_CreateUser {

  private final UserRepository userRepository;
  private final EmailValidatorClient emailValidatorClient;
  private final AvatarClient avatarClient;

  @PostMapping("/users")
  public User createUser(@RequestBody CreateUserRequest request) {
    var emailValid = emailValidatorClient.isEmailValid(request.email());
    if (!emailValid) {
      throw new InvalidEmailException(request.email());
    }
    var avatarUrl = avatarClient.getRandomAvatarUrl();
    return userRepository.save(new User(request.email(), avatarUrl));
  }

  public record CreateUserRequest(String email) {

  }

}
