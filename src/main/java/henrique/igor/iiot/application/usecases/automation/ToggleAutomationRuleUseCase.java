package henrique.igor.iiot.application.usecases.automation;

import henrique.igor.iiot.domain.entities.AutomationRule;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AutomationRuleRepository;

import java.util.UUID;

public class ToggleAutomationRuleUseCase {

    private final AutomationRuleRepository automationRuleRepository;

    public ToggleAutomationRuleUseCase(AutomationRuleRepository automationRuleRepository) {
        this.automationRuleRepository = automationRuleRepository;
    }

    public AutomationRule execute(UUID automationId) {
        if (automationId == null) {
            throw new DomainValidationException("AutomationRule ID cannot be null.");
        }

        AutomationRule rule = automationRuleRepository.findById(automationId)
                .orElseThrow(() -> new ResourceNotFoundException("AutomationRule", automationId));

        rule.toggle();

        return automationRuleRepository.save(rule);
    }
}
