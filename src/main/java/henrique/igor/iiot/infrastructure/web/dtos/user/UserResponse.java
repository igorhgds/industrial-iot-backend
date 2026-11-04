package henrique.igor.iiot.infrastructure.web.dtos.user;

import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.entities.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "User representation returned by the API")
public record UserResponse(
        @Schema(description = "Unique identifier of the user", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,

        @Schema(description = "Full name of the user", example = "Carlos Mendes")
        String name,

        @Schema(description = "Corporate email address", example = "carlos.mendes@industry.com")
        String email,

        @Schema(description = "Role assigned to the user", example = "TECHNICIAN")
        UserRole userRole,

        @Schema(description = "Identifier of the sector the user is allocated to", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectorId,

        @Schema(description = "Name of the sector the user is allocated to", example = "Usinagem")
        String sectorName,

        @Schema(description = "Timestamp when the user was created", example = "2026-10-06T12:00:00Z")
        OffsetDateTime createdAt
) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getUserRole(),
                user.getSector() != null ? user.getSector().getSectorId() : null,
                user.getSector() != null ? user.getSector().getName() : null,
                user.getCreatedAt()
        );
    }
}
