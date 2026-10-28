package henrique.igor.iiot.application.usecases.workOrder.dto;

import java.util.UUID;

public record CloseWorkOrderInput(
        UUID workOrderId,
        String resolutionNotes
) {
}
