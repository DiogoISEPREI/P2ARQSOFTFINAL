package pt.psoft.g1.psoftg1.readermanagement.model.message;

import java.util.UUID;
import java.util.List;

public record ReaderCreatedMessage(
        String username,
        String fullName,
        String birthDate,
        String phoneNumber,
        boolean gdprConsent,
        boolean marketingConsent,
        boolean thirdPartySharingConsent,
        String photoUri,
        List<String> interestList,
        UUID correlationId
) {
}
