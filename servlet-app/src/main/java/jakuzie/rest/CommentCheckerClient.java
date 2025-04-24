package jakuzie.rest;

import jakuzie.config.AppProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CommentCheckerClient {

  private final RestTemplate restTemplate;
  private final AppProperties appProperties;

  public boolean isCommentSafe(String postContent, String comment) {
    var response = restTemplate.postForObject(appProperties.getRemoteServiceUrl() + "/safety-check",
        new SafetyRequest(postContent, comment), SafetyResponse.class);
    return Optional.ofNullable(response).map(SafetyResponse::safe).orElse(false);
  }

  public boolean isAuthorUnlocked(String postAuthor, String commentAuthor) {
    var response = restTemplate.postForObject(appProperties.getRemoteServiceUrl() + "/author-check",
        new AuthorRequest(postAuthor, commentAuthor), AuthorResponse.class);
    return Optional.ofNullable(response).map(AuthorResponse::unlocked).orElse(false);
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
