package henrique.igor.iiot.infrastructure.messaging.dto;

import henrique.igor.iiot.domain.entities.enums.Severity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AlertTriggeredEventDTO(
        UUID alertId,
        UUID equipmentId,
        String equipmentCode,
        UUID ruleGroupId,
        String ruleGroupTitle,
        Severity severity,
        String message,
        OffsetDateTime triggeredAt,
        List<AlertSensorSnapshotEventDTO> sensorSnapshots
) {
}
