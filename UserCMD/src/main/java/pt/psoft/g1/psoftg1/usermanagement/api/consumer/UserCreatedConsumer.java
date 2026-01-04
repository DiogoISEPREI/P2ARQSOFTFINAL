package pt.psoft.g1.psoftg1.usermanagement.api.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.configuration.RabbitMQConfig;
import pt.psoft.g1.psoftg1.usermanagement.model.message.UserCreatedMessage;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;

@Component
public class UserCreatedConsumer {

    private final UserService userService;

    public UserCreatedConsumer(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void consume(UserCreatedMessage message) {

        userService.createFromEvent(
                message.username(),
                message.name(),
                message.role()
        );
    }
}
