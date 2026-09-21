package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;
import henrique.igor.iiot.domain.repositories.WorkOrderRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.WorkOrderJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.WorkOrderMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.WorkOrderJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WorkOrderPersistenceAdapter implements WorkOrderRepository {

    private final WorkOrderJpaRepository workOrderJpaRepository;

    public WorkOrderPersistenceAdapter(WorkOrderJpaRepository workOrderJpaRepository) {
        this.workOrderJpaRepository = workOrderJpaRepository;
    }

    @Override
    public WorkOrder save(WorkOrder workOrder) {
        WorkOrderJpaEntity jpaEntity = WorkOrderMapper.toJpaEntity(workOrder);
        WorkOrderJpaEntity savedEntity = workOrderJpaRepository.save(jpaEntity);
        return WorkOrderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<WorkOrder> findById(UUID workOrderId) {
        return workOrderJpaRepository.findById(workOrderId)
                .map(WorkOrderMapper::toDomain);
    }

    @Override
    public List<WorkOrder> findByEquipmentId(UUID equipmentId) {
        return workOrderJpaRepository.findByEquipmentEquipmentId(equipmentId)
                .stream()
                .map(WorkOrderMapper::toDomain)
                .toList();
    }

    @Override
    public List<WorkOrder> findByStatus(WorkOrderStatus status) {
        return workOrderJpaRepository.findByStatus(status)
                .stream()
                .map(WorkOrderMapper::toDomain)
                .toList();
    }

    @Override
    public List<WorkOrder> findAll() {
        return workOrderJpaRepository.findAll()
                .stream()
                .map(WorkOrderMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID workOrderId) {
        workOrderJpaRepository.deleteById(workOrderId);
    }
}
