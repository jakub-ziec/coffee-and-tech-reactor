package jakuzie.fixtures

import groovy.util.logging.Slf4j
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentLinkedQueue

@Component
@Slf4j
class TestRabbitListener {

    def userCreatedEvents = new ConcurrentLinkedQueue<String>()

    private final RabbitTemplate rabbitTemplate
    private final AmqpAdmin admin

    TestRabbitListener(RabbitTemplate rabbitTemplate, AmqpAdmin admin) {
        this.rabbitTemplate = rabbitTemplate
        this.admin = admin
    }

    @RabbitListener(queues = "user-created-event")
    void onUserCreatedEvent(String event) {
        log.info("Received User Created event: {}", event)
        userCreatedEvents.add(event)
    }

    void reset() {
        admin.purgeQueue("user-created-event")
        userCreatedEvents.clear()
    }

}
