package henrique.igor.iiot.infrastructure.web.dtos.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.CreateWorkOrderInput;
import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for creating a new maintenance Work Order")
public record CreateWorkOrderRequest(
        @Schema(description = "Title of the maintenance work order", example = "Substituição de rolamento do motor principal")
        @NotBlank(message = "WorkOrder title is required")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,

        @Schema(description = "Detailed description of the problem and maintenance instructions", example = "Vibração acima do limite ISO 20816 detectada pelo sensor VIB-001")
        String description,

        @Schema(
                description = "Optional priority. If omitted, derived automatically from the linked alert severity or defaults to MEDIUM",
                example = "HIGH"
        )
        WorkOrderPriority priority,

        @Schema(description = "Identifier of the target equipment", example = "770e8400-e29b-41d4-a716-446655440222")
        @NotNull(message = "Equipment ID is required")
        UUID equipmentId,

        @Schema(description = "Optional identifier of the anomaly alert that originated this work order", example = "aa0e8400-e29b-41d4-a716-446655440555")
        UUID alertId,

        @Schema(description = "Optional identifier of the technician user assigned to the work order", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID assignedToUserId
) {
    public CreateWorkOrderInput toInput() {
        return new CreateWorkOrderInput(title, description, priority, equipmentId, alertId, assignedToUserId);
    }
}
