package henrique.igor.iiot.infrastructure.web.dtos.equipmentState;

import henrique.igor.iiot.domain.entities.EquipmentState;
import henrique.igor.iiot.domain.entities.enums.HealthStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Current real-time health state of an industrial equipment (O(1) operational projection)")
public record EquipmentStateResponse(
        @Schema(description = "Unique identifier of the equipment", example = "770e8400-e29b-41d4-a716-446655440222")
        UUID equipmentId,

        @Schema(description = "Business code of the equipment", example = "MTR-USI-001")
        String equipCode,

        @Schema(description = "Calculated health status based on dynamic rules evaluation", example = "OPERATIONAL")
        HealthStatus status,

        @Schema(description = "Raw JSON payload of the latest telemetry reading", example = "{\"equipCode\":\"MTR-USI-001\",\"readings\":[{\"sensorCode\":\"TEM-001\",\"value\":68.3}]}")
        String lastPayload,

        @Schema(description = "Timestamp of the last telemetry state update", example = "2026-10-06T12:30:03Z")
        OffsetDateTime updatedAt
) {
    public static EquipmentStateResponse fromDomain(EquipmentState state) {
        return new EquipmentStateResponse(
                state.getEquipmentId() != null ? state.getEquipmentId().getEquipmentId() : null,
                state.getEquipmentId() != null ? state.getEquipmentId().getEquipCode() : null,
                state.getStatus(),
                state.getLastPayload(),
                state.getUpdatedAt()
        );
    }
}
