package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.AutomationRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AutomationRuleRepository {

    AutomationRule save(AutomationRule automationRule);

    Optional<AutomationRule> findById(UUID automationId);

    List<AutomationRule> findByIsActiveTrue();

    List<AutomationRule> findAll();

    void deleteById(UUID automationId);
}
