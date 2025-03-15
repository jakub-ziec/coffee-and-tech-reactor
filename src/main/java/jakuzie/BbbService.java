package jakuzie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class BbbService {

  public Mono<String> doSth() {
    return Mono.fromSupplier(() -> "B");
  }

}
