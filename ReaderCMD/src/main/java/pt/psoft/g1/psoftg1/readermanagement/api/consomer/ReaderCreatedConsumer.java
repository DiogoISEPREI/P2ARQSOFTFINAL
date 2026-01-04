package pt.psoft.g1.psoftg1.readermanagement.api.consomer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.configuration.RabbitMQConfig;
import pt.psoft.g1.psoftg1.readermanagement.model.message.ReaderCreatedMessage;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderServiceImpl;

@Component
public class ReaderCreatedConsumer {

    private final ReaderServiceImpl readerService;

    public ReaderCreatedConsumer(ReaderServiceImpl readerService) {
        this.readerService = readerService;
    }

    @RabbitListener(queues = RabbitMQConfig.READER_CREATED_QUEUE)
    public void consume(ReaderCreatedMessage message) {

        readerService.createFromEvent(
                message.username(),
                message.fullName(),
                message.birthDate(),
                message.phoneNumber(),
                message.gdprConsent(),
                message.marketingConsent(),
                message.thirdPartySharingConsent(),
                message.photoUri(),
                message.interestList()
        );
    }
}
