package jakuzie.email;

import jakuzie.mongo.SendEmailOutbox;
import jakuzie.mongo.SendEmailOutboxRepository;
import jakuzie.mongo.User;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSender {

  private final SendEmailOutboxRepository outboxRepository;

  @Setter
  private int delay = 0;

  public Mono<String> sendEmail(WelcomeEmail email) {
    log.info("Sending email: {}", email);
    return waitABit()
        .then(outboxRepository.save(new SendEmailOutbox(email.toAddress(), "Welcome", "Welcome to our app!")))
        .map(SendEmailOutbox::id)
        .doOnNext(id -> log.info("Email sent: {}, id={}", email, id));
  }

  private Mono<Void> waitABit() {
    return Mono.delay(Duration.ofMillis(delay))
        .doOnNext(aLong -> log.info("Delay added: {} ms", delay))
        .then();
  }

  public record WelcomeEmail(String userId, String toAddress) {

    public WelcomeEmail(User user) {
      this(user.getId().toString(), user.getEmail());
    }
  }
}
