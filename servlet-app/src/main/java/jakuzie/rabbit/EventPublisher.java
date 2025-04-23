package jakuzie.rabbit;

import jakuzie.mongo.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

  public static final String POST_CREATED_QUEUE = "post-created-event";

  private final RabbitTemplate rabbitTemplate;

  public void publish(PostCreated event) {
    log.info("Publishing event to RabbitMQ: {}", event);
    rabbitTemplate.convertAndSend(POST_CREATED_QUEUE, event.postId().getBytes());
    log.info("Published event to RabbitMQ: {}", event);
  }

  public record PostCreated(String postId) {

    public PostCreated(Post post) {
      this(post.getId().toString());
    }
  }

}
