package henrique.igor.iiot.application.usecases.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.WorkOrderSensorSnapshotOutput;
import henrique.igor.iiot.application.usecases.workOrder.dto.WorkOrderSheetOutput;
import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.WorkOrderRepository;

import java.util.List;
import java.util.UUID;

public class GetWorkOrderSheetUseCase {

    private final WorkOrderRepository workOrderRepository;

    public GetWorkOrderSheetUseCase(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    public WorkOrderSheetOutput execute(UUID workOrderId) {
        if (workOrderId == null) {
            throw new DomainValidationException("WorkOrder ID cannot be null.");
        }

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", workOrderId));

        Equipment equipment = workOrder.getEquipment();
        User technician = workOrder.getAssignedTo();
        Alert alert = workOrder.getAlert();

        List<WorkOrderSensorSnapshotOutput> snapshots = (alert != null && alert.getSensorSnapshots() != null)
                ? alert.getSensorSnapshots().stream()
                .map(s -> new WorkOrderSensorSnapshotOutput(
                        s.getSensor() != null ? s.getSensor().getCode() : null,
                        s.getSensor() != null ? s.getSensor().getSensorType() : null,
                        s.getSensor() != null ? s.getSensor().getUnitOfMeasure() : null,
                        s.getValueAtTrigger()
                ))
                .toList()
                : List.of();

        return new WorkOrderSheetOutput(
                workOrder.getWorkOrderId(),
                workOrder.getTitle(),
                workOrder.getDescription(),
                workOrder.getPriority(),
                workOrder.getStatus(),
                workOrder.getCreatedAt(),
                workOrder.getClosedAt(),
                equipment != null ? equipment.getEquipmentId() : null,
                equipment != null ? equipment.getEquipCode() : null,
                equipment != null ? equipment.getType() : null,
                (equipment != null && equipment.getSector() != null) ? equipment.getSector().getName() : null,
                (equipment != null && equipment.getGateway() != null) ? equipment.getGateway().getGatewayCode() : null,
                technician != null ? technician.getUserId() : null,
                technician != null ? technician.getName() : null,
                technician != null ? technician.getEmail() : null,
                alert != null ? alert.getAlertId() : null,
                (alert != null && alert.getRuleGroup() != null) ? alert.getRuleGroup().getName() : null,
                alert != null ? alert.getSeverity() : null,
                alert != null ? alert.getMessage() : null,
                alert != null ? alert.getTriggeredAt() : null,
                snapshots
        );
    }
}
