package henrique.igor.iiot.infrastructure.web.dtos.user;

import henrique.igor.iiot.application.usecases.user.dto.UpdateUserInput;
import henrique.igor.iiot.domain.entities.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for updating an existing user")
public record UpdateUserRequest(
        @Schema(description = "Updated full name of the user", example = "Carlos Mendes Silva")
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        String name,

        @Schema(description = "Updated corporate email address", example = "carlos.silva@industry.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @Schema(description = "Updated role assigned to the user", example = "MANAGER")
        @NotNull(message = "User role is required")
        UserRole userRole,

        @Schema(description = "Optional sector ID to relocate the user", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectorId
) {
    public UpdateUserInput toInput(UUID userId) {
        return new UpdateUserInput(
                userId,
                name,
                email,
                userRole,
                sectorId
        );
    }
}
