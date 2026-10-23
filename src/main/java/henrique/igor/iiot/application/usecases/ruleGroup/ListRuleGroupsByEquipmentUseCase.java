package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;

import java.util.List;
import java.util.UUID;

public class ListRuleGroupsByEquipmentUseCase {

    private final RuleGroupRepository ruleGroupRepository;
    private final EquipmentRepository equipmentRepository;

    public ListRuleGroupsByEquipmentUseCase(
            RuleGroupRepository ruleGroupRepository,
            EquipmentRepository equipmentRepository
    ) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public List<RuleGroup> execute(UUID equipmentId) {
        if (equipmentId == null) {
            throw new DomainValidationException("Equipment ID cannot be null.");
        }

        equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId));

        return ruleGroupRepository.findByEquipmentId(equipmentId);
    }
}
