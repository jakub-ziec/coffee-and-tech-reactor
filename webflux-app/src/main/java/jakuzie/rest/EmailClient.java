package jakuzie.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import jakuzie.config.AppProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EmailClient {
  private final WebClient webClient;
  private final AppProperties appProperties;

  public Mono<Boolean> isEmailValid(String email) {
    return webClient.post().uri(appProperties.getRemoteServiceUrl() + "/validate-email")
        .contentType(APPLICATION_JSON)
        .bodyValue(new Request(email))
        .retrieve()
        .bodyToMono(Response.class)
        .map(Response::valid);
  }

  record Request(String email) {
  }

  record Response(boolean valid) {
  }

}
