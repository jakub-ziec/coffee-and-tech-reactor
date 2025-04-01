package jakuzie.rabbit;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
@Slf4j
public class RabbitConfig {

  public static final String USER_CREATED_QUEUE= "user-created-event";

  @Autowired
  public void init(AmqpAdmin amqpAdmin) {
    amqpAdmin.declareQueue(new Queue(USER_CREATED_QUEUE, false, false, false));
  }

  @Bean
  public Mono<Connection> connectionMono(RabbitProperties rabbitProperties) {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost(rabbitProperties.getHost());
    connectionFactory.setPort(rabbitProperties.getPort());
    connectionFactory.setUsername(rabbitProperties.getUsername());
    connectionFactory.setPassword(rabbitProperties.getPassword());
    return Mono.fromCallable(() -> connectionFactory.newConnection("reactor-rabbit")).cache();
  }

  @Bean
  Sender sender(Mono<Connection> connectionMono) {
    return RabbitFlux.createSender(new SenderOptions().connectionMono(connectionMono));
  }

//  public record LikeEvent(Long postId, int likes) {
//  }

//  @Bean
//  public Consumer<Flux<LikeEvent>> onLikeEvent() {
//    return flux -> flux
//        .doOnNext(event -> log.info("received event: {}", event))
//        .onErrorContinue((exception, element) -> log.error("Pretend nothing happened, to NOT disconnect from Rabbit "+exception.getMessage()+ " " + element))
//        .subscribe();
//  }

}