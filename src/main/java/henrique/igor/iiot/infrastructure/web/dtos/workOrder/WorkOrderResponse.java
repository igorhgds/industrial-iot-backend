package henrique.igor.iiot.infrastructure.web.dtos.workOrder;

import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Maintenance Work Order representation returned by the API")
public record WorkOrderResponse(
        @Schema(description = "Unique identifier of the work order", example = "bb0e8400-e29b-41d4-a716-446655440666")
        UUID workOrderId,

        @Schema(description = "Title of the work order", example = "Substituição de rolamento do motor principal")
        String title,

        @Schema(description = "Description and resolution notes", example = "Vibração acima do limite ISO 20816 detectada pelo sensor VIB-001")
        String description,

        @Schema(description = "Priority level", example = "HIGH")
        WorkOrderPriority priority,

        @Schema(description = "Current lifecycle status", example = "OPEN")
        WorkOrderStatus status,

        @Schema(description = "Identifier of the equipment under maintenance", example = "770e8400-e29b-41d4-a716-446655440222")
        UUID equipmentId,

        @Schema(description = "Business code of the equipment", example = "MTR-USI-001")
        String equipCode,

        @Schema(description = "Identifier of the linked anomaly alert, if any", example = "aa0e8400-e29b-41d4-a716-446655440555")
        UUID alertId,

        @Schema(description = "Identifier of the assigned technician", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID assignedToUserId,

        @Schema(description = "Name of the assigned technician", example = "Carlos Mendes")
        String assignedToUserName,

        @Schema(description = "Timestamp when the work order was created", example = "2026-10-06T12:00:00Z")
        OffsetDateTime createdAt,

        @Schema(description = "Timestamp when the work order was closed/cancelled", example = "2026-10-06T15:30:00Z")
        OffsetDateTime closedAt
) {
    public static WorkOrderResponse fromDomain(WorkOrder workOrder) {
        return new WorkOrderResponse(
                workOrder.getWorkOrderId(),
                workOrder.getTitle(),
                workOrder.getDescription(),
                workOrder.getPriority(),
                workOrder.getStatus(),
                workOrder.getEquipment() != null ? workOrder.getEquipment().getEquipmentId() : null,
                workOrder.getEquipment() != null ? workOrder.getEquipment().getEquipCode() : null,
                workOrder.getAlert() != null ? workOrder.getAlert().getAlertId() : null,
                workOrder.getAssignedTo() != null ? workOrder.getAssignedTo().getUserId() : null,
                workOrder.getAssignedTo() != null ? workOrder.getAssignedTo().getName() : null,
                workOrder.getCreatedAt(),
                workOrder.getClosedAt()
        );
    }
}
