package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;
import henrique.igor.iiot.infrastructure.persistence.entities.WorkOrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkOrderJpaRepository extends JpaRepository<WorkOrderJpaEntity, UUID> {

    List<WorkOrderJpaEntity> findByEquipmentEquipmentId(UUID equipmentId);

    List<WorkOrderJpaEntity> findByStatus(WorkOrderStatus status);
}
