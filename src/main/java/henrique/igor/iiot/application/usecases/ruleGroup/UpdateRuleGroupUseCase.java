package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.UpdateRuleGroupInput;
import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceAlreadyExistsException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.RuleConditionRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;

import java.util.List;

public class UpdateRuleGroupUseCase {

    private final RuleGroupRepository ruleGroupRepository;
    private final RuleConditionRepository ruleConditionRepository;

    public UpdateRuleGroupUseCase(
            RuleGroupRepository ruleGroupRepository,
            RuleConditionRepository ruleConditionRepository
    ) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleConditionRepository = ruleConditionRepository;
    }

    public RuleGroup execute(UpdateRuleGroupInput input) {
        if (input == null || input.ruleGroupId() == null) {
            throw new DomainValidationException("RuleGroup ID cannot be null.");
        }

        RuleGroup existingRuleGroup = ruleGroupRepository.findById(input.ruleGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("RuleGroup", input.ruleGroupId()));

        if (input.name() != null && !input.name().isBlank() &&
                !existingRuleGroup.getName().equalsIgnoreCase(input.name())) {
            ruleGroupRepository.findByNameAndEquipmentId(
                    input.name(),
                    existingRuleGroup.getEquipment().getEquipmentId()
            ).ifPresent(rg -> {
                throw new ResourceAlreadyExistsException("RuleGroup", "name", input.name());
            });
        }

        existingRuleGroup.updateInfo(
                input.name(),
                input.description(),
                input.severity(),
                input.operatorType()
        );

        if (input.isActive() != null) {
            if (input.isActive()) {
                List<RuleCondition> conditions = ruleConditionRepository.findByRuleGroupId(input.ruleGroupId());
                if (conditions.isEmpty()) {
                    throw new DomainValidationException("Cannot activate a RuleGroup without at least one condition.");
                }
                existingRuleGroup.activate();
            } else {
                existingRuleGroup.deactivate();
            }
        }

        return ruleGroupRepository.save(existingRuleGroup);
    }
}
