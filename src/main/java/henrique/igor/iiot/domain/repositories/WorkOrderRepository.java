package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkOrderRepository {

    WorkOrder save(WorkOrder workOrder);

    Optional<WorkOrder> findById(UUID workOrderId);

    List<WorkOrder> findByEquipmentId(UUID equipmentId);

    List<WorkOrder> findByStatus(WorkOrderStatus status);

    List<WorkOrder> findAll();

    void deleteById(UUID workOrderId);
}
