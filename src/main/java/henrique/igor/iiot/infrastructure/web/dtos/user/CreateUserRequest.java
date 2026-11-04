package henrique.igor.iiot.infrastructure.web.dtos.user;

import henrique.igor.iiot.application.usecases.user.dto.CreateUserInput;
import henrique.igor.iiot.domain.entities.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for registering a new user")
public record CreateUserRequest(
        @Schema(description = "Full name of the user", example = "Carlos Mendes")
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        String name,

        @Schema(description = "Unique corporate email address", example = "carlos.mendes@industry.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @Schema(description = "User account password", example = "StrongPass@123")
        @NotBlank(message = "Password is required")
        @Size(max = 255, message = "Password must not exceed 255 characters")
        String password,

        @Schema(description = "Optional 8-character password recovery code", example = "REC12345")
        @Size(max = 8, message = "Password recovery code must not exceed 8 characters")
        String passwordRecovery,

        @Schema(description = "Role assigned to the user", example = "TECHNICIAN")
        @NotNull(message = "User role is required")
        UserRole userRole,

        @Schema(description = "Optional sector ID to which the user belongs", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectorId
) {
    public CreateUserInput toInput() {
        return new CreateUserInput(
                name,
                email,
                password,
                passwordRecovery,
                userRole,
                sectorId
        );
    }
}
