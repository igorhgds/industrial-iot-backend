package henrique.igor.iiot.infrastructure.web.dtos.equipment;

import henrique.igor.iiot.application.usecases.equipment.dto.RegisterEquipmentInput;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for registering a new industrial equipment")
public record RegisterEquipmentRequest(
        @Schema(
                description = "Optional custom equipment code. If omitted or blank, a code is generated automatically",
                example = "MTR-USI-001"
        )
        @Size(max = 50, message = "Equipment code must not exceed 50 characters")
        String equipCode,

        @Schema(description = "Type of industrial equipment", example = "MOTOR")
        @NotNull(message = "Equipment type is required")
        EquipType type,

        @Schema(description = "Optional sector ID where the equipment is installed", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectorId,

        @Schema(description = "Optional gateway ID connected to the equipment", example = "660e8400-e29b-41d4-a716-446655440111")
        UUID gatewayId
) {
    public RegisterEquipmentInput toInput() {
        return new RegisterEquipmentInput(equipCode, type, sectorId, gatewayId);
    }
}
