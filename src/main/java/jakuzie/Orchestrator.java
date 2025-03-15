package jakuzie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class Orchestrator {

  private final AaaService aaaService;
  private final BbbService bbbService;

  public Mono<String> orchestrate() {
    return aaaService.doSth()
        .then(bbbService.doSth());
  }

  public Mono<String> orchestrateWithRetry() {
    return aaaService.doSth()
        .retry(1)
        .then(bbbService.doSth());
  }
}
