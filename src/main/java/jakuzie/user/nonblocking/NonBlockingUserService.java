package jakuzie.user.nonblocking;

import jakuzie.user.InvalidEmailException;
import jakuzie.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NonBlockingUserService {

  private final NonBlockingEmailService emailService;
  private final NonBlockingAvatarService avatarService;
  private final NonBlockingUserRepository userRepository;

  public Mono<User> createUser(String email, long delay) {
    return emailService.isEmailValid(email, delay)
        .doOnNext(emailValid -> {
          if (!emailValid) {
            throw new InvalidEmailException(email);
          }
        })
        .then(avatarService.getRandomAvatarUrl(delay))
        .flatMap(avatarUrl -> userRepository.save(User.newUser(email, avatarUrl)));
  }


}
