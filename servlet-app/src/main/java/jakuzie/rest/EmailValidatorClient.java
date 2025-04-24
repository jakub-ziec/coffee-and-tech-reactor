package jakuzie.rest;

import jakuzie.config.AppProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EmailValidatorClient {
  private final RestTemplate restTemplate;
  private final AppProperties appProperties;

  public boolean isEmailValid(String email) {
    Response response = restTemplate.postForObject(appProperties.getRemoteServiceUrl() + "/validate-email", new Request(email), Response.class);
    return Optional.ofNullable(response).map(Response::valid).orElse(false);
  }

  record Request(String email) {
  }

  record Response(boolean valid) {
  }

}
