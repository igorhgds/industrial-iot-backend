package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.RuleCondition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleConditionRepository {

    RuleCondition save(RuleCondition condition);

    Optional<RuleCondition> findById(UUID conditionId);

    List<RuleCondition> findByRuleGroupId(UUID ruleGroupId);

    List<RuleCondition> findAll();

    void deleteById(UUID conditionId);
}
