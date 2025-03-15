package jakuzie.user.blocking;

import jakuzie.user.InvalidEmailException;
import jakuzie.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockingUserService {

  private final BlockingEmailService emailService;
  private final BlockingAvatarService avatarService;
  private final BlockingUserRepository userRepository;

  public User createUser(String email, long delay) {
    var emailValid = emailService.isEmailValid(email, delay); // takes delay ms
    if (!emailValid) {
      throw new InvalidEmailException(email);
    }
    var avatarUrl = avatarService.getRandomAvatarUrl(delay); // takes delay ms
    return userRepository.save(User.newUser(email, avatarUrl));
  }

}
