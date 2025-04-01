package jakuzie.rabbit;

import static jakuzie.rabbit.RabbitConfig.USER_CREATED_QUEUE;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

  public Mono<Void> publish(UserCreated event) {
    log.info("Publishing event to RabbitMQ: {}", event);
    OutboundMessage outboundMessage = new OutboundMessage("", USER_CREATED_QUEUE, event.userId().toString().getBytes());
    return sender.sendWithPublishConfirms(Mono.just(outboundMessage), new SendOptions())
        .doOnNext(id -> log.info("Published event to RabbitMQ: {}", event))
        .then();
  }

  public record UserCreated(UUID userId) {

  }

}
