package henrique.igor.iiot.infrastructure.web.dtos.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.CloseWorkOrderInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Payload for closing a Work Order with technical resolution notes")
public record CloseWorkOrderRequest(
        @Schema(description = "Technical resolution notes describing the maintenance action performed", example = "Rolamento dianteiro substituído e alinhamento a laser realizado.")
        @NotBlank(message = "Resolution notes are required")
        String resolutionNotes
) {
    public CloseWorkOrderInput toInput(UUID workOrderId) {
        return new CloseWorkOrderInput(workOrderId, resolutionNotes);
    }
}
