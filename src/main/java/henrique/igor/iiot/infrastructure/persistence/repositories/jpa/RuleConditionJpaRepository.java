package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.infrastructure.persistence.entities.RuleConditionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RuleConditionJpaRepository extends JpaRepository<RuleConditionJpaEntity, UUID> {

    List<RuleConditionJpaEntity> findByRuleGroupRuleGroupId(UUID ruleGroupId);
}
