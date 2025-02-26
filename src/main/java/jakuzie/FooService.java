package jakuzie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FooService {

  private final BarClient barClient;

  public Mono<Void> doSth(boolean callBar) {
    return Mono.just(!callBar)
        .filter(Boolean::booleanValue)
        .switchIfEmpty(barClient.doSth().thenReturn(true))
        .then();
  }

}
