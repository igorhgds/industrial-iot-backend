package henrique.igor.iiot.application.usecases.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.UpdateWorkOrderStatusInput;
import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.WorkOrderRepository;

public class UpdateWorkOrderStatusUseCase {

    private final WorkOrderRepository workOrderRepository;

    public UpdateWorkOrderStatusUseCase(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    public WorkOrder execute(UpdateWorkOrderStatusInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.workOrderId() == null) {
            throw new DomainValidationException("WorkOrder ID cannot be null.");
        }
        if (input.newStatus() == null) {
            throw new DomainValidationException("WorkOrder status cannot be null.");
        }

        WorkOrder workOrder = workOrderRepository.findById(input.workOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", input.workOrderId()));

        workOrder.transitionStatus(input.newStatus());

        return workOrderRepository.save(workOrder);
    }
}
