package henrique.igor.iiot.infrastructure.web.dtos.equipment;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Industrial equipment representation returned by the API")
public record EquipmentResponse(
        @Schema(description = "Unique identifier of the equipment", example = "770e8400-e29b-41d4-a716-446655440222")
        UUID equipmentId,

        @Schema(description = "Unique business code of the equipment", example = "MTR-USI-001")
        String equipCode,

        @Schema(description = "Type of the equipment", example = "MOTOR")
        EquipType type,

        @Schema(description = "Current operational status", example = "ACTIVE")
        EquipStatus status,

        @Schema(description = "Identifier of the sector where the equipment is installed", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID sectorId,

        @Schema(description = "Name of the sector where the equipment is installed", example = "Usinagem")
        String sectorName,

        @Schema(description = "Identifier of the gateway connected to the equipment", example = "660e8400-e29b-41d4-a716-446655440111")
        UUID gatewayId,

        @Schema(description = "Code of the gateway connected to the equipment", example = "GW-USI-001")
        String gatewayCode,

        @Schema(description = "Timestamp when the equipment was registered", example = "2026-10-06T12:00:00Z")
        OffsetDateTime createdAt
) {
    public static EquipmentResponse fromDomain(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getEquipmentId(),
                equipment.getEquipCode(),
                equipment.getType(),
                equipment.getStatus(),
                equipment.getSector() != null ? equipment.getSector().getSectorId() : null,
                equipment.getSector() != null ? equipment.getSector().getName() : null,
                equipment.getGateway() != null ? equipment.getGateway().getGatewayId() : null,
                equipment.getGateway() != null ? equipment.getGateway().getGatewayCode() : null,
                equipment.getCreatedAt()
        );
    }
}
