package henrique.igor.iiot.application.usecases.workOrder.dto;

import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;

import java.util.UUID;

public record CreateWorkOrderInput(
        String title,
        String description,
        WorkOrderPriority priority,
        UUID equipmentId,
        UUID alertId,
        UUID assignedToUserId
) {
}
