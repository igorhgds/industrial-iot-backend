package henrique.igor.iiot.infrastructure.web.dtos.alert;

import henrique.igor.iiot.application.usecases.alert.dto.TriggerAlertInput;
import henrique.igor.iiot.domain.entities.enums.Severity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Schema(description = "Payload for triggering a new multi-sensor anomaly Alert")
public record TriggerAlertRequest(
        @Schema(description = "Identifier of the equipment experiencing the anomaly", example = "770e8400-e29b-41d4-a716-446655440222")
        @NotNull(message = "Equipment ID is required")
        UUID equipmentId,

        @Schema(description = "Identifier of the breached Rule Group", example = "cc0e8400-e29b-41d4-a716-446655440777")
        @NotNull(message = "RuleGroup ID is required")
        UUID ruleGroupId,

        @Schema(
                description = "Optional severity override. If omitted or REGULAR, derived from the Rule Group severity",
                example = "CRITICAL"
        )
        Severity severity,

        @Schema(description = "Human-readable alert diagnostic message", example = "Temperatura do estator (88.5°C) excedeu o limite crítico de 85.0°C")
        @NotBlank(message = "Alert message is required")
        String message,

        @Schema(description = "List of sensor reading snapshots captured at trigger time")
        @NotEmpty(message = "At least one sensor snapshot is required")
        @Valid
        List<SensorSnapshotRequest> sensorSnapshots
) {
    public TriggerAlertInput toInput() {
        return new TriggerAlertInput(
                equipmentId,
                ruleGroupId,
                severity,
                message,
                sensorSnapshots != null
                        ? sensorSnapshots.stream().map(SensorSnapshotRequest::toInput).toList()
                        : List.of()
        );
    }
}
