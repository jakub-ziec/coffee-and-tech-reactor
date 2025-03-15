package jakuzie.rest;

import jakuzie.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AvatarClient {
  private final WebClient webClient;
  private final AppProperties appProperties;

  public Mono<String> getRandomAvatarUrl() {
    return webClient.get()
        .uri(appProperties.getRemoteServiceUrl() + "/random-avatar")
        .retrieve()
        .bodyToMono(Response.class)
        .map(Response::avatarUrl);
  }

  record Response(String avatarUrl) {
  }

}
