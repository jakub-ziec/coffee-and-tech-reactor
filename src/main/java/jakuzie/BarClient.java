package jakuzie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class BarClient {

  public Mono<Void> doSth() {
    return Mono.fromRunnable(() -> {
      log.info("Starting doing sth");
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error("Error while doing sth", e);
      }
      log.info("Finished doing sth");
    });
  }

}
