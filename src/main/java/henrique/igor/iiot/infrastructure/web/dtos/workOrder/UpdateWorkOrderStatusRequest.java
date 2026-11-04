package henrique.igor.iiot.infrastructure.web.dtos.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.UpdateWorkOrderStatusInput;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Payload for transitioning the status of a Work Order")
public record UpdateWorkOrderStatusRequest(
        @Schema(description = "Target status in the Work Order lifecycle", example = "IN_PROGRESS")
        @NotNull(message = "New status is required")
        WorkOrderStatus newStatus
) {
    public UpdateWorkOrderStatusInput toInput(UUID workOrderId) {
        return new UpdateWorkOrderStatusInput(workOrderId, newStatus);
    }
}
