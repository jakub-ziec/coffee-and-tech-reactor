package jakuzie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AaaService {

  public Mono<String> doSth() {
    return Mono.fromSupplier(() -> "A");
  }

}
