package jakuzie.user.blocking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockingAvatarService {

  public String getRandomAvatarUrl(long delay) {
    log.info("Starting");
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      log.error("Error", e);
    }
    log.info("Finished");
    return "https://placehold.co/400";
  }

}
