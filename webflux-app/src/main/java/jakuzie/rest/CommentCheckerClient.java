package jakuzie.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import jakuzie.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentCheckerClient {

  private final WebClient webClient;
  private final AppProperties appProperties;

  public Mono<Boolean> isCommentSafe(String postContent, String comment) {
    return webClient.post().uri(appProperties.getRemoteServiceUrl() + "/safety-check")
        .contentType(APPLICATION_JSON)
        .bodyValue(new SafetyRequest(postContent, comment))
        .retrieve()
        .bodyToMono(SafetyResponse.class)
        .map(SafetyResponse::safe);
  }

  public Mono<Boolean> isAuthorUnlocked(String postAuthor, String commentAuthor) {
    return webClient.post().uri(appProperties.getRemoteServiceUrl() + "/author-check")
        .contentType(APPLICATION_JSON)
        .bodyValue(new AuthorRequest(postAuthor, commentAuthor))
        .retrieve()
        .bodyToMono(AuthorResponse.class)
        .map(AuthorResponse::unlocked);
  }

  record SafetyRequest(String postContent, String comment) {
  }

  record SafetyResponse(boolean safe) {
  }

  record AuthorRequest(String postAuthor, String commentAuthor) {
  }

  record AuthorResponse(boolean unlocked) {
  }

}
