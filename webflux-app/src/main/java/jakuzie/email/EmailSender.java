package jakuzie.email;

import jakuzie.mongo.SendEmailOutbox;
import jakuzie.mongo.SendEmailOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSender {

  private final SendEmailOutboxRepository outboxRepository;

  public Mono<String> sendEmail(WelcomeEmail email) {
    log.info("Sending email: {}", email);
    return outboxRepository.save(new SendEmailOutbox(email.toAddress(), "Welcome", "Welcome to our app!"))
        .map(SendEmailOutbox::id)
        .doOnNext(id -> log.info("Email sent: {}, id={}", email, id));
  }

  public record WelcomeEmail(String toAddress) {

  }
}
