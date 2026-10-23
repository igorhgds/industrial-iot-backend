package henrique.igor.iiot.application.usecases.alert.dto;

import henrique.igor.iiot.domain.entities.enums.Severity;

import java.util.List;
import java.util.UUID;

public record TriggerAlertInput(
        UUID equipmentId,
        UUID ruleGroupId,
        Severity severity,
        String message,
        List<SensorSnapshotInput> sensorSnapshots
) {
}
