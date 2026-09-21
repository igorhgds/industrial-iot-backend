package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.infrastructure.persistence.entities.RuleGroupJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RuleGroupJpaRepository extends JpaRepository<RuleGroupJpaEntity, UUID> {

    List<RuleGroupJpaEntity> findByEquipmentEquipmentId(UUID equipmentId);

    List<RuleGroupJpaEntity> findByIsActiveTrue();
}
