package jakuzie.fixtures

import groovy.util.logging.Slf4j
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentLinkedQueue

@Component
@Slf4j
class TestRabbitListener {

    def userCreatedEvents = new ConcurrentLinkedQueue<String>()

    @RabbitListener(queues = "user-created-event")
    void onUserCreatedEvent(String event) {
        log.info("Received User Created event: {}", event)
        userCreatedEvents.add(event)
    }

}
