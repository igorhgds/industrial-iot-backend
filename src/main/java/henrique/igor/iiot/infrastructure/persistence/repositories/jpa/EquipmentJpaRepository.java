package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.infrastructure.persistence.entities.EquipmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipmentJpaRepository extends JpaRepository<EquipmentJpaEntity, UUID> {

    Optional<EquipmentJpaEntity> findByEquipCode(String equipCode);
}
