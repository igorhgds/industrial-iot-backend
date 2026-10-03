package henrique.igor.iiot.application.usecases.user.dto;

import henrique.igor.iiot.domain.entities.enums.UserRole;

import java.util.UUID;

public record UpdateUserInput(
        UUID userId,
        String name,
        String email,
        UserRole userRole,
        UUID sectorId
) {
}
