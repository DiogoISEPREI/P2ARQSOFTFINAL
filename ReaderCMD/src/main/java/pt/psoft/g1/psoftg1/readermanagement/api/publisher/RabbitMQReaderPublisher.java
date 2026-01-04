package pt.psoft.g1.psoftg1.readermanagement.api.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.configuration.RabbitMQConfig;
import pt.psoft.g1.psoftg1.readermanagement.model.message.ReaderCreatedMessage;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderMessagePublisher;

@Component
public class RabbitMQReaderPublisher implements ReaderMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQReaderPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishReaderCreated(ReaderCreatedMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.READER_EXCHANGE,
                RabbitMQConfig.READER_CREATED_ROUTING_KEY,
                message
        );
    }
}
