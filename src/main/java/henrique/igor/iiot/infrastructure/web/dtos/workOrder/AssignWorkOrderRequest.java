package henrique.igor.iiot.infrastructure.web.dtos.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.AssignWorkOrderInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Payload for assigning a technician to a Work Order")
public record AssignWorkOrderRequest(
        @Schema(description = "UUID of the user with TECHNICIAN role", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull(message = "Technician ID is required")
        UUID technicianId
) {
    public AssignWorkOrderInput toInput(UUID workOrderId) {
        return new AssignWorkOrderInput(workOrderId, technicianId);
    }
}
