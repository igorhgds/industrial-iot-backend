package henrique.igor.iiot.application.usecases.workOrder.dto;

import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record WorkOrderSheetOutput(
        UUID workOrderId,
        String title,
        String description,
        WorkOrderPriority priority,
        WorkOrderStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime closedAt,
        UUID equipmentId,
        String equipmentCode,
        EquipType equipmentType,
        String sectorName,
        String gatewayCode,
        UUID technicianId,
        String technicianName,
        String technicianEmail,
        UUID alertId,
        String ruleGroupName,
        Severity alertSeverity,
        String alertMessage,
        OffsetDateTime alertTriggeredAt,
        List<WorkOrderSensorSnapshotOutput> sensorSnapshots
) {
}
