package henrique.igor.iiot.application.usecases.user.dto;

import henrique.igor.iiot.domain.entities.enums.UserRole;

import java.util.UUID;

public record CreateUserRequest(
        String name,
        String email,
        String password,
        String passwordRecovery,
        UserRole userRole,
        UUID sectorId
) {
}
