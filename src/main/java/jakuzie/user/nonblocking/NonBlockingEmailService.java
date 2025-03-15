package jakuzie.user.nonblocking;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class NonBlockingEmailService {

  public Mono<Boolean> isEmailValid(String email, long delay) {
    return Mono.fromSupplier(() -> {
      log.info("Starting");
      return true;
    })
        .delayElement(Duration.ofMillis(delay))
        .doOnNext(it -> log.info("Finished"));
  }

}
