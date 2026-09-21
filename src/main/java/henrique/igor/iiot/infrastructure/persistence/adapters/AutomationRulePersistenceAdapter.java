package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.AutomationRule;
import henrique.igor.iiot.domain.repositories.AutomationRuleRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.AutomationRuleJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.AutomationRuleMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.AutomationRuleJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AutomationRulePersistenceAdapter implements AutomationRuleRepository {

    private final AutomationRuleJpaRepository automationRuleJpaRepository;

    public AutomationRulePersistenceAdapter(AutomationRuleJpaRepository automationRuleJpaRepository) {
        this.automationRuleJpaRepository = automationRuleJpaRepository;
    }

    @Override
    public AutomationRule save(AutomationRule automationRule) {
        AutomationRuleJpaEntity jpaEntity = AutomationRuleMapper.toJpaEntity(automationRule);
        AutomationRuleJpaEntity savedEntity = automationRuleJpaRepository.save(jpaEntity);
        return AutomationRuleMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AutomationRule> findById(UUID automationId) {
        return automationRuleJpaRepository.findById(automationId)
                .map(AutomationRuleMapper::toDomain);
    }

    @Override
    public List<AutomationRule> findByIsActiveTrue() {
        return automationRuleJpaRepository.findByIsActiveTrue()
                .stream()
                .map(AutomationRuleMapper::toDomain)
                .toList();
    }

    @Override
    public List<AutomationRule> findAll() {
        return automationRuleJpaRepository.findAll()
                .stream()
                .map(AutomationRuleMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID automationId) {
        automationRuleJpaRepository.deleteById(automationId);
    }
}
