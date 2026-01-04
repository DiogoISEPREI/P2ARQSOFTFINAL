package pt.psoft.g1.psoftg1.usermanagement.api.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.configuration.RabbitMQConfig;
import pt.psoft.g1.psoftg1.usermanagement.model.message.UserCreatedMessage;
import pt.psoft.g1.psoftg1.usermanagement.services.UserMessagePublisher;

@Component
public class RabbitMQUserPublisher implements UserMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQUserPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishUserCreated(UserCreatedMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                RabbitMQConfig.USER_CREATED_ROUTING_KEY,
                message
        );
    }
}
