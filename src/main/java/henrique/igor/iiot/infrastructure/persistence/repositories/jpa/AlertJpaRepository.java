package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.infrastructure.persistence.entities.AlertJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertJpaRepository extends JpaRepository<AlertJpaEntity, UUID> {

    List<AlertJpaEntity> findByEquipmentEquipmentId(UUID equipmentId);

    List<AlertJpaEntity> findByStatus(AlertStatus status);
}
