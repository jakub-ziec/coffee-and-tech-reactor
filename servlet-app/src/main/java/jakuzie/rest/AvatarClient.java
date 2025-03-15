package jakuzie.rest;

import jakuzie.config.AppProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AvatarClient {
  private final RestTemplate restTemplate;
  private final AppProperties appProperties;

  public String getRandomAvatarUrl() {
    Response response = restTemplate.getForObject(appProperties.getRemoteServiceUrl() + "/random-avatar", Response.class);
    return Optional.ofNullable(response).map(Response::avatarUrl).orElse(null);
  }

  record Response(String avatarUrl) {
  }

}
