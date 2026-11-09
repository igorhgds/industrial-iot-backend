package henrique.igor.iiot.infrastructure.messaging.dto;

import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkOrderCreatedEventDTO(
        UUID workOrderId,
        UUID equipmentId,
        String equipmentCode,
        UUID alertId,
        UUID assignedToUserId,
        String title,
        String description,
        WorkOrderPriority priority,
        WorkOrderStatus status,
        OffsetDateTime createdAt
) {
}
