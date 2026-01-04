package pt.psoft.g1.psoftg1.usermanagement.model.message;

import java.util.UUID;

public record UserCreatedMessage(
        String username,
        String name,
        String role,
        UUID correlationId
) {
}
