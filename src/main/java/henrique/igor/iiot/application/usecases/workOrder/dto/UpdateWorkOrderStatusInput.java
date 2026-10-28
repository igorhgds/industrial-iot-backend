package henrique.igor.iiot.application.usecases.workOrder.dto;

import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;

import java.util.UUID;

public record UpdateWorkOrderStatusInput(
        UUID workOrderId,
        WorkOrderStatus newStatus
) {
}
