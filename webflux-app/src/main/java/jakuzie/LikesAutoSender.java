package jakuzie;

import static reactor.core.publisher.Mono.just;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class LikesAutoSender {
  private final Sender sender;
  private static final AtomicInteger counter = new AtomicInteger(1);

  //  @Scheduled(fixedRate = 300)
  public void sendPostCreatedEvent() {
//    log.info("Sending post created event");
    Long postId = new Random().nextLong(4) + 2;
    if (Math.random()<0.5) postId = null;
    String json = """
        {"postId":%s, "likes":%d}""".formatted(postId, counter.addAndGet(new Random().nextInt(100)));
    OutboundMessage message = new OutboundMessage("", "likes.flux", json.getBytes());
    sender.sendWithPublishConfirms(just(message))
//        .doOnNext(res -> log.info("event sent"))
        .blockLast(); // It's OK to .block() in scheduler
  }

}
