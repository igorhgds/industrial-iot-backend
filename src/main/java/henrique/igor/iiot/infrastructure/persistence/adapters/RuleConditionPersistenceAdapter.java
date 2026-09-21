package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.repositories.RuleConditionRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.RuleConditionJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.RuleConditionMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.RuleConditionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RuleConditionPersistenceAdapter implements RuleConditionRepository {

    private final RuleConditionJpaRepository ruleConditionJpaRepository;

    public RuleConditionPersistenceAdapter(RuleConditionJpaRepository ruleConditionJpaRepository) {
        this.ruleConditionJpaRepository = ruleConditionJpaRepository;
    }

    @Override
    public RuleCondition save(RuleCondition condition) {
        RuleConditionJpaEntity jpaEntity = RuleConditionMapper.toJpaEntity(condition);
        RuleConditionJpaEntity savedEntity = ruleConditionJpaRepository.save(jpaEntity);
        return RuleConditionMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RuleCondition> findById(UUID conditionId) {
        return ruleConditionJpaRepository.findById(conditionId)
                .map(RuleConditionMapper::toDomain);
    }

    @Override
    public List<RuleCondition> findByRuleGroupId(UUID ruleGroupId) {
        return ruleConditionJpaRepository.findByRuleGroupRuleGroupId(ruleGroupId)
                .stream()
                .map(RuleConditionMapper::toDomain)
                .toList();
    }

    @Override
    public List<RuleCondition> findAll() {
        return ruleConditionJpaRepository.findAll()
                .stream()
                .map(RuleConditionMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID conditionId) {
        ruleConditionJpaRepository.deleteById(conditionId);
    }
}
