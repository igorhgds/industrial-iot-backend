package henrique.igor.iiot.domain.ports;

import henrique.igor.iiot.domain.entities.WorkOrder;

public interface WorkOrderEventPublisher {

    void publishWorkOrderCreated(WorkOrder workOrder);
}
