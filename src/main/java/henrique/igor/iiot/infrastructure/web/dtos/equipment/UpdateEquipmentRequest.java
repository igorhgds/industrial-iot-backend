package henrique.igor.iiot.infrastructure.web.dtos.equipment;

import henrique.igor.iiot.application.usecases.equipment.dto.UpdateEquipmentInput;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Payload for updating an existing industrial equipment")
public record UpdateEquipmentRequest(
        @Schema(description = "Operational status of the equipment", example = "MAINTENANCE")
        EquipStatus status,

        @Schema(description = "New sector ID to relocate the equipment", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectorId,

        @Schema(description = "New gateway ID to connect the equipment", example = "660e8400-e29b-41d4-a716-446655440111")
        UUID gatewayId
) {
    public UpdateEquipmentInput toInput(UUID equipmentId) {
        return new UpdateEquipmentInput(equipmentId, status, sectorId, gatewayId);
    }
}
