package henrique.igor.iiot.infrastructure.web.dtos.alert;

import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.domain.entities.enums.Severity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Anomaly Alert representation returned by the API")
public record AlertResponse(
        @Schema(description = "Unique identifier of the alert", example = "aa0e8400-e29b-41d4-a716-446655440555")
        UUID alertId,

        @Schema(description = "Identifier of the equipment", example = "770e8400-e29b-41d4-a716-446655440222")
        UUID equipmentId,

        @Schema(description = "Code of the equipment", example = "MTR-USI-001")
        String equipCode,

        @Schema(description = "Identifier of the breached Rule Group", example = "cc0e8400-e29b-41d4-a716-446655440777")
        UUID ruleGroupId,

        @Schema(description = "Name of the breached Rule Group", example = "Sobrecarga Térmica e Vibração")
        String ruleGroupName,

        @Schema(description = "Alert severity", example = "CRITICAL")
        Severity severity,

        @Schema(description = "Diagnostic message", example = "Temperatura do estator (88.5°C) excedeu o limite crítico de 85.0°C")
        String message,

        @Schema(description = "Current lifecycle status of the alert", example = "PENDING")
        AlertStatus status,

        @Schema(description = "Identifier of the user who acknowledged the alert", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID acknowledgedByUserId,

        @Schema(description = "Name of the user who acknowledged the alert", example = "Carlos Mendes")
        String acknowledgedByUserName,

        @Schema(description = "Timestamp when the alert was triggered", example = "2026-10-06T12:00:00Z")
        OffsetDateTime triggeredAt,

        @Schema(description = "Timestamp when the alert was resolved/dismissed", example = "2026-10-06T14:00:00Z")
        OffsetDateTime resolvedAt,

        @Schema(description = "Sensor snapshots captured when the alert was triggered")
        List<AlertSensorSnapshotResponse> sensorSnapshots
) {
    public static AlertResponse fromDomain(Alert alert) {
        List<AlertSensorSnapshotResponse> snapshots = alert.getSensorSnapshots() != null
                ? alert.getSensorSnapshots().stream().map(AlertSensorSnapshotResponse::fromDomain).toList()
                : List.of();

        return new AlertResponse(
                alert.getAlertId(),
                alert.getEquipment() != null ? alert.getEquipment().getEquipmentId() : null,
                alert.getEquipment() != null ? alert.getEquipment().getEquipCode() : null,
                alert.getRuleGroup() != null ? alert.getRuleGroup().getRuleGroupId() : null,
                alert.getRuleGroup() != null ? alert.getRuleGroup().getName() : null,
                alert.getSeverity(),
                alert.getMessage(),
                alert.getStatus(),
                alert.getAcknowledgedBy() != null ? alert.getAcknowledgedBy().getUserId() : null,
                alert.getAcknowledgedBy() != null ? alert.getAcknowledgedBy().getName() : null,
                alert.getTriggeredAt(),
                alert.getResolvedAt(),
                snapshots
        );
    }
}
