package jakuzie;

import jakarta.annotation.PostConstruct;
import jakuzie.mongo.Post;
import jakuzie.mongo.PostRepository;
import jakuzie.rabbit.EventPublisher;
import jakuzie.rabbit.EventPublisher.PostLiked;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class LikesAutoSender {

  private static final AtomicInteger counter = new AtomicInteger(1);
  private static final Random random = new Random();
  private static final List<String> postIds = List.of(
      "11111111-1111-1111-1111-111111111111",
      "22222222-2222-2222-2222-222222222222",
      "33333333-3333-3333-3333-333333333333",
      "44444444-4444-4444-4444-444444444444",
      "wrong-formated-id"
  );

  private final EventPublisher eventPublisher;
  private final PostRepository postRepository;

  @PostConstruct
  public void initPosts() {
    postRepository.deleteAll().block();
    postRepository.saveAll(postIds.stream().limit(4)
            .map(id -> new Post(UUID.fromString(id), "title-" + id.charAt(0), "content", "author"))
            .toList())
        .blockLast();
  }


  @Scheduled(initialDelay = 100, fixedRate = 300)
  public void sendPostCreatedEvent() {
    String postId = postIds.get(random.nextInt(postIds.size()));
    int likes = counter.addAndGet(random.nextInt(100));
    eventPublisher.publish(new PostLiked(postId, likes)).block();
  }

}
