package jakuzie.rabbit;

import static jakuzie.rabbit.RabbitConfig.POST_CREATED_QUEUE;
import static jakuzie.rabbit.RabbitConfig.USER_CREATED_QUEUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakuzie.mongo.Post;
import jakuzie.mongo.User;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.SendOptions;
import reactor.rabbitmq.Sender;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

  private final Sender sender;
  private final ObjectMapper objectMapper;

  @Setter
  private String postCreatedQueue = POST_CREATED_QUEUE;

  public Mono<Void> publish(UserCreated event) {
    log.info("Publishing event to RabbitMQ: {}", event);
    OutboundMessage outboundMessage = new OutboundMessage("", USER_CREATED_QUEUE, event.userId().toString().getBytes());
    return sender.sendWithPublishConfirms(Mono.just(outboundMessage), new SendOptions())
        .doOnNext(id -> log.info("Published event to RabbitMQ: {}", event))
        .then();
  }

  public Mono<Void> publish(PostCreated event) {
    log.info("Publishing event to RabbitMQ: {}", event);
    OutboundMessage outboundMessage = new OutboundMessage("", postCreatedQueue, event.postId().getBytes());
    return sender.sendWithPublishConfirms(Mono.just(outboundMessage), new SendOptions())
        .doOnNext(id -> log.info("Published event to RabbitMQ: {}", event))
        .then();
  }

  public Mono<Void> publish(Tagged event) {
    log.info("Publishing event to RabbitMQ: {}", event);
    return sender.sendWithPublishConfirms(createMessage("", "tags.flux",event), new SendOptions())
        .doOnNext(id -> log.info("Published event to RabbitMQ: {}", event))
        .then();
  }

  public Mono<Void> publish(PostLiked event) {
//    log.info("Publishing event to RabbitMQ: {}", event);
    return sender.sendWithPublishConfirms(createMessage("", "likes.flux",event), new SendOptions())
//        .doOnNext(id -> log.info("Published event to RabbitMQ: {}", event))
        .then();
  }

  private Mono<OutboundMessage> createMessage(String exchange, String routingKey, Object payload) {
    return asJsonBytes(payload)
        .map(bytes -> new OutboundMessage(exchange, routingKey, bytes));
  }

  private Mono<byte[]> asJsonBytes(Object object) {
    return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(object))
        .doOnError(e -> log.error("Failed to serialize object to JSON", e));
  }

  public record UserCreated(UUID userId) {
    public UserCreated(User user) {
      this(user.getId());
    }
  }

  public record PostCreated(String postId) {

    public PostCreated(Post post) {
      this(post.getId().toString());
    }
  }

  public record Tagged(String targetId, List<String> tags) {

  }

  public record PostLiked(String postId, int likes) {}

}
