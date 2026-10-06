package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.infrastructure.persistence.entities.EquipmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface EquipmentJpaRepository extends JpaRepository<EquipmentJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"sector", "gateway"})
    Optional<EquipmentJpaEntity> findByEquipCode(String equipCode);

    long countBySectorSectorIdAndType(UUID sectorId, EquipType type);

    long countBySectorIsNullAndType(EquipType type);
}
