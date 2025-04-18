package jakuzie.fixtures

import groovy.util.logging.Slf4j
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentLinkedQueue

@Component
@Slf4j
class TestRabbitListener {

    def userCreatedEvents = new ConcurrentLinkedQueue<String>()

    private final RabbitTemplate rabbitTemplate

    TestRabbitListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate
    }

    @RabbitListener(queues = "user-created-event")
    void onUserCreatedEvent(String event) {
        log.info("Received User Created event: {}", event)
        userCreatedEvents.add(event)
    }

    void reset() {
        while (rabbitTemplate.receive("user-created-event", 200) != null) {
            print(".")
        }
        println("")
        userCreatedEvents.clear()
    }

}
