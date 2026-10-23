package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.RuleConditionRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;

import java.util.List;
import java.util.UUID;

public class ActivateRuleGroupUseCase {

    private final RuleGroupRepository ruleGroupRepository;
    private final RuleConditionRepository ruleConditionRepository;

    public ActivateRuleGroupUseCase(
            RuleGroupRepository ruleGroupRepository,
            RuleConditionRepository ruleConditionRepository
    ) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleConditionRepository = ruleConditionRepository;
    }

    public RuleGroup execute(UUID ruleGroupId) {
        if (ruleGroupId == null) {
            throw new DomainValidationException("RuleGroup ID cannot be null.");
        }

        RuleGroup ruleGroup = ruleGroupRepository.findById(ruleGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("RuleGroup", ruleGroupId));

        List<RuleCondition> conditions = ruleConditionRepository.findByRuleGroupId(ruleGroupId);
        if (conditions.isEmpty()) {
            throw new DomainValidationException("Cannot activate a RuleGroup without at least one condition.");
        }

        ruleGroup.activate();

        return ruleGroupRepository.save(ruleGroup);
    }
}
