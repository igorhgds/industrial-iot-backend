package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.RuleGroupJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.RuleGroupMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.RuleGroupJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RuleGroupPersistenceAdapter implements RuleGroupRepository {

    private final RuleGroupJpaRepository ruleGroupJpaRepository;

    public RuleGroupPersistenceAdapter(RuleGroupJpaRepository ruleGroupJpaRepository) {
        this.ruleGroupJpaRepository = ruleGroupJpaRepository;
    }

    @Override
    public RuleGroup save(RuleGroup ruleGroup) {
        RuleGroupJpaEntity jpaEntity = RuleGroupMapper.toJpaEntity(ruleGroup);
        RuleGroupJpaEntity savedEntity = ruleGroupJpaRepository.save(jpaEntity);
        return RuleGroupMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RuleGroup> findById(UUID ruleGroupId) {
        return ruleGroupJpaRepository.findById(ruleGroupId)
                .map(RuleGroupMapper::toDomain);
    }

    @Override
    public List<RuleGroup> findByEquipmentId(UUID equipmentId) {
        return ruleGroupJpaRepository.findByEquipmentEquipmentId(equipmentId)
                .stream()
                .map(RuleGroupMapper::toDomain)
                .toList();
    }

    @Override
    public List<RuleGroup> findByIsActiveTrue() {
        return ruleGroupJpaRepository.findByIsActiveTrue()
                .stream()
                .map(RuleGroupMapper::toDomain)
                .toList();
    }

    @Override
    public List<RuleGroup> findAll() {
        return ruleGroupJpaRepository.findAll()
                .stream()
                .map(RuleGroupMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID ruleGroupId) {
        ruleGroupJpaRepository.deleteById(ruleGroupId);
    }
}
