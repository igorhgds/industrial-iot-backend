package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.RuleGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleGroupRepository {

    RuleGroup save(RuleGroup ruleGroup);

    Optional<RuleGroup> findById(UUID ruleGroupId);

    List<RuleGroup> findByEquipmentId(UUID equipmentId);

    List<RuleGroup> findByIsActiveTrue();

    List<RuleGroup> findAll();

    void deleteById(UUID ruleGroupId);
}
