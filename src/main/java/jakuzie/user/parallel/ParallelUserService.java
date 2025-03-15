package jakuzie.user.parallel;

import jakuzie.user.InvalidEmailException;
import jakuzie.user.User;
import jakuzie.user.nonblocking.NonBlockingAvatarService;
import jakuzie.user.nonblocking.NonBlockingEmailService;
import jakuzie.user.nonblocking.NonBlockingUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ParallelUserService {

  private final NonBlockingEmailService emailService;
  private final NonBlockingAvatarService avatarService;
  private final NonBlockingUserRepository userRepository;

  public Mono<User> createUser(String email, long delay) {
    return Mono.zip(
            emailService.isEmailValid(email, delay),
            avatarService.getRandomAvatarUrl(delay))
        .flatMap(tuple -> {
          boolean emailValid = tuple.getT1();
          String avatarUrl = tuple.getT2();
          if (!emailValid) {
            return Mono.error(new InvalidEmailException(email));
          }
          return userRepository.save(User.newUser(email, avatarUrl));
        });
  }

}
