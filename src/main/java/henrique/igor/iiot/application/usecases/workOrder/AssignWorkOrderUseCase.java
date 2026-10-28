package henrique.igor.iiot.application.usecases.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.AssignWorkOrderInput;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.UserRepository;
import henrique.igor.iiot.domain.repositories.WorkOrderRepository;

public class AssignWorkOrderUseCase {

    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;

    public AssignWorkOrderUseCase(WorkOrderRepository workOrderRepository, UserRepository userRepository) {
        this.workOrderRepository = workOrderRepository;
        this.userRepository = userRepository;
    }

    public WorkOrder execute(AssignWorkOrderInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.workOrderId() == null) {
            throw new DomainValidationException("WorkOrder ID cannot be null.");
        }
        if (input.technicianId() == null) {
            throw new DomainValidationException("Technician ID cannot be null.");
        }

        WorkOrder workOrder = workOrderRepository.findById(input.workOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", input.workOrderId()));

        User technician = userRepository.findById(input.technicianId())
                .orElseThrow(() -> new ResourceNotFoundException("User", input.technicianId()));

        workOrder.assignToTechnician(technician);

        return workOrderRepository.save(workOrder);
    }
}
