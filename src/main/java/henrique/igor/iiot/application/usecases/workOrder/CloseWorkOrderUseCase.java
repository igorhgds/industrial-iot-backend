package henrique.igor.iiot.application.usecases.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.CloseWorkOrderInput;
import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AlertRepository;
import henrique.igor.iiot.domain.repositories.WorkOrderRepository;

public class CloseWorkOrderUseCase {

    private final WorkOrderRepository workOrderRepository;
    private final AlertRepository alertRepository;

    public CloseWorkOrderUseCase(WorkOrderRepository workOrderRepository, AlertRepository alertRepository) {
        this.workOrderRepository = workOrderRepository;
        this.alertRepository = alertRepository;
    }

    public WorkOrder execute(CloseWorkOrderInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.workOrderId() == null) {
            throw new DomainValidationException("WorkOrder ID cannot be null.");
        }
        if (input.resolutionNotes() == null || input.resolutionNotes().isBlank()) {
            throw new DomainValidationException("Resolution notes cannot be null or blank when closing a WorkOrder.");
        }

        WorkOrder workOrder = workOrderRepository.findById(input.workOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder", input.workOrderId()));

        workOrder.completeWithResolutionNotes(input.resolutionNotes());

        if (workOrder.getAlert() != null && workOrder.getAlert().getAlertId() != null) {
            Alert linkedAlert = alertRepository.findById(workOrder.getAlert().getAlertId())
                    .orElse(workOrder.getAlert());

            if (linkedAlert.getStatus() != AlertStatus.RESOLVED) {
                linkedAlert.dismiss();
                alertRepository.save(linkedAlert);
            }
        }

        return workOrderRepository.save(workOrder);
    }
}
