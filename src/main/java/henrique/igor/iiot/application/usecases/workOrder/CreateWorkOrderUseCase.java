package henrique.igor.iiot.application.usecases.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.CreateWorkOrderInput;
import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AlertRepository;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.UserRepository;
import henrique.igor.iiot.domain.repositories.WorkOrderRepository;

public class CreateWorkOrderUseCase {

    private final WorkOrderRepository workOrderRepository;
    private final EquipmentRepository equipmentRepository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    public CreateWorkOrderUseCase(
            WorkOrderRepository workOrderRepository,
            EquipmentRepository equipmentRepository,
            AlertRepository alertRepository,
            UserRepository userRepository
    ) {
        this.workOrderRepository = workOrderRepository;
        this.equipmentRepository = equipmentRepository;
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
    }

    public WorkOrder execute(CreateWorkOrderInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.equipmentId() == null) {
            throw new DomainValidationException("Equipment ID cannot be null.");
        }
        if (input.title() == null || input.title().isBlank()) {
            throw new DomainValidationException("WorkOrder title cannot be null or blank.");
        }

        Equipment equipment = equipmentRepository.findById(input.equipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", input.equipmentId()));

        Alert alert = null;
        if (input.alertId() != null) {
            alert = alertRepository.findById(input.alertId())
                    .orElseThrow(() -> new ResourceNotFoundException("Alert", input.alertId()));

            if (alert.getEquipment() == null ||
                    !alert.getEquipment().getEquipmentId().equals(equipment.getEquipmentId())) {
                throw new DomainValidationException("Alert does not belong to the specified Equipment.");
            }
        }

        WorkOrderPriority priority = resolvePriority(input.priority(), alert);

        WorkOrder workOrder = new WorkOrder(
                input.title().trim(),
                input.description(),
                priority,
                alert,
                equipment,
                null
        );

        if (input.assignedToUserId() != null) {
            User technician = userRepository.findById(input.assignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", input.assignedToUserId()));
            workOrder.assignToTechnician(technician);
        }

        return workOrderRepository.save(workOrder);
    }

    private WorkOrderPriority resolvePriority(WorkOrderPriority inputPriority, Alert alert) {
        if (inputPriority != null) {
            return inputPriority;
        }
        if (alert != null && alert.getSeverity() != null) {
            return switch (alert.getSeverity()) {
                case CRITICAL -> WorkOrderPriority.CRITICAL;
                case WARNING -> WorkOrderPriority.HIGH;
                case INFO, REGULAR -> WorkOrderPriority.MEDIUM;
            };
        }
        return WorkOrderPriority.MEDIUM;
    }
}
