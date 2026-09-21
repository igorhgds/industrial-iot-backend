package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.infrastructure.persistence.entities.AutomationRuleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AutomationRuleJpaRepository extends JpaRepository<AutomationRuleJpaEntity, UUID> {

    List<AutomationRuleJpaEntity> findByIsActiveTrue();
}
