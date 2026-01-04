package pt.psoft.g1.psoftg1.usermanagement.services;

import pt.psoft.g1.psoftg1.usermanagement.model.message.UserCreatedMessage;

public interface UserMessagePublisher {

    void publishUserCreated(UserCreatedMessage message);

}
