package jakuzie.user.blocking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BlockingEmailService {

  public boolean isEmailValid(String email, long delay) {
    log.info("Starting");
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      log.error("Error", e);
    }
    log.info("Finished");
    return true;
  }

}
