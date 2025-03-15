package jakuzie.user.nonblocking;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class NonBlockingAvatarService {

  public Mono<String> getRandomAvatarUrl(long delay) {
    return Mono.fromSupplier(() -> {
      log.info("Starting");
      return "https://placehold.co/400";
    })
        .delayElement(Duration.ofMillis(delay))
        .doOnNext(it -> log.info("Finished"));
  }

}
