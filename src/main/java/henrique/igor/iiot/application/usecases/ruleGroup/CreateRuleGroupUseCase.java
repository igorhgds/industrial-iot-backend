package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.CreateRuleGroupInput;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceAlreadyExistsException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;

public class CreateRuleGroupUseCase {

    private final RuleGroupRepository ruleGroupRepository;
    private final EquipmentRepository equipmentRepository;

    public CreateRuleGroupUseCase(RuleGroupRepository ruleGroupRepository, EquipmentRepository equipmentRepository) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public RuleGroup execute(CreateRuleGroupInput input) {
        if (input.equipmentId() == null) {
            throw new DomainValidationException("Equipment ID cannot be null.");
        }

        Equipment equipment = equipmentRepository.findById(input.equipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", input.equipmentId()));

        if (ruleGroupRepository.findByNameAndEquipmentId(input.name(), input.equipmentId()).isPresent()) {
            throw new ResourceAlreadyExistsException("RuleGroup", "name", input.name());
        }

        RuleGroup newRuleGroup = new RuleGroup(
                input.name(),
                input.description(),
                equipment,
                input.operatorType()
        );

        return ruleGroupRepository.save(newRuleGroup);
    }
}
