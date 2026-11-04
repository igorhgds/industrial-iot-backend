package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.ruleGroup.ActivateRuleGroupUseCase;
import henrique.igor.iiot.application.usecases.ruleGroup.AddConditionToRuleGroupUseCase;
import henrique.igor.iiot.application.usecases.ruleGroup.CreateRuleGroupUseCase;
import henrique.igor.iiot.application.usecases.ruleGroup.ListRuleGroupsByEquipmentUseCase;
import henrique.igor.iiot.application.usecases.ruleGroup.UpdateRuleGroupUseCase;
import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.infrastructure.web.dtos.ruleGroup.AddConditionToRuleGroupRequest;
import henrique.igor.iiot.infrastructure.web.dtos.ruleGroup.CreateRuleGroupRequest;
import henrique.igor.iiot.infrastructure.web.dtos.ruleGroup.RuleConditionResponse;
import henrique.igor.iiot.infrastructure.web.dtos.ruleGroup.RuleGroupResponse;
import henrique.igor.iiot.infrastructure.web.dtos.ruleGroup.UpdateRuleGroupRequest;
import henrique.igor.iiot.infrastructure.web.specs.RuleGroupControllerSpec;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rule-groups")
public class RuleGroupController implements RuleGroupControllerSpec {

    private final CreateRuleGroupUseCase createRuleGroupUseCase;
    private final ListRuleGroupsByEquipmentUseCase listRuleGroupsByEquipmentUseCase;
    private final UpdateRuleGroupUseCase updateRuleGroupUseCase;
    private final ActivateRuleGroupUseCase activateRuleGroupUseCase;
    private final AddConditionToRuleGroupUseCase addConditionToRuleGroupUseCase;

    public RuleGroupController(
            CreateRuleGroupUseCase createRuleGroupUseCase,
            ListRuleGroupsByEquipmentUseCase listRuleGroupsByEquipmentUseCase,
            UpdateRuleGroupUseCase updateRuleGroupUseCase,
            ActivateRuleGroupUseCase activateRuleGroupUseCase,
            AddConditionToRuleGroupUseCase addConditionToRuleGroupUseCase
    ) {
        this.createRuleGroupUseCase = createRuleGroupUseCase;
        this.listRuleGroupsByEquipmentUseCase = listRuleGroupsByEquipmentUseCase;
        this.updateRuleGroupUseCase = updateRuleGroupUseCase;
        this.activateRuleGroupUseCase = activateRuleGroupUseCase;
        this.addConditionToRuleGroupUseCase = addConditionToRuleGroupUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<RuleGroupResponse> create(@Valid @RequestBody CreateRuleGroupRequest request) {
        RuleGroup createdRuleGroup = createRuleGroupUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(RuleGroupResponse.fromDomain(createdRuleGroup));
    }

    @Override
    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<RuleGroupResponse>> listByEquipment(@PathVariable UUID equipmentId) {
        List<RuleGroupResponse> ruleGroups = listRuleGroupsByEquipmentUseCase.execute(equipmentId).stream()
                .map(RuleGroupResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(ruleGroups);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<RuleGroupResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRuleGroupRequest request
    ) {
        RuleGroup updatedRuleGroup = updateRuleGroupUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(RuleGroupResponse.fromDomain(updatedRuleGroup));
    }

    @Override
    @PatchMapping("/{id}/activate")
    public ResponseEntity<RuleGroupResponse> activate(@PathVariable UUID id) {
        RuleGroup activatedRuleGroup = activateRuleGroupUseCase.execute(id);
        return ResponseEntity.ok(RuleGroupResponse.fromDomain(activatedRuleGroup));
    }

    @Override
    @PostMapping("/{id}/conditions")
    public ResponseEntity<RuleConditionResponse> addCondition(
            @PathVariable UUID id,
            @Valid @RequestBody AddConditionToRuleGroupRequest request
    ) {
        RuleCondition createdCondition = addConditionToRuleGroupUseCase.execute(request.toInput(id));
        return ResponseEntity.status(HttpStatus.CREATED).body(RuleConditionResponse.fromDomain(createdCondition));
    }
}
