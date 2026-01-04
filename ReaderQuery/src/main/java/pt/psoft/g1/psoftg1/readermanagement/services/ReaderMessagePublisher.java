package pt.psoft.g1.psoftg1.readermanagement.services;

import pt.psoft.g1.psoftg1.readermanagement.model.message.ReaderCreatedMessage;

public interface ReaderMessagePublisher {

    void publishReaderCreated(ReaderCreatedMessage message);
}
