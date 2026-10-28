package henrique.igor.iiot.application.usecases.workOrder.dto;

import java.util.UUID;

public record AssignWorkOrderInput(
        UUID workOrderId,
        UUID technicianId
) {
}
