package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.automation.CreateAutomationRuleUseCase;
import henrique.igor.iiot.application.usecases.automation.ExecuteActuationCommandUseCase;
import henrique.igor.iiot.application.usecases.automation.ToggleAutomationRuleUseCase;
import henrique.igor.iiot.application.usecases.automation.dto.ActuationCommandResult;
import henrique.igor.iiot.domain.entities.AutomationRule;
import henrique.igor.iiot.infrastructure.web.dtos.automation.AutomationRuleResponse;
import henrique.igor.iiot.infrastructure.web.dtos.automation.CreateAutomationRuleRequest;
import henrique.igor.iiot.infrastructure.web.dtos.automation.ExecuteActuationCommandRequest;
import henrique.igor.iiot.infrastructure.web.specs.AutomationRuleControllerSpec;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/automation-rules")
public class AutomationRuleController implements AutomationRuleControllerSpec {

    private final CreateAutomationRuleUseCase createAutomationRuleUseCase;
    private final ToggleAutomationRuleUseCase toggleAutomationRuleUseCase;
    private final ExecuteActuationCommandUseCase executeActuationCommandUseCase;

    public AutomationRuleController(
            CreateAutomationRuleUseCase createAutomationRuleUseCase,
            ToggleAutomationRuleUseCase toggleAutomationRuleUseCase,
            ExecuteActuationCommandUseCase executeActuationCommandUseCase
    ) {
        this.createAutomationRuleUseCase = createAutomationRuleUseCase;
        this.toggleAutomationRuleUseCase = toggleAutomationRuleUseCase;
        this.executeActuationCommandUseCase = executeActuationCommandUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<AutomationRuleResponse> create(@Valid @RequestBody CreateAutomationRuleRequest request) {
        AutomationRule createdRule = createAutomationRuleUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(AutomationRuleResponse.fromDomain(createdRule));
    }

    @Override
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<AutomationRuleResponse> toggle(@PathVariable UUID id) {
        AutomationRule toggledRule = toggleAutomationRuleUseCase.execute(id);
        return ResponseEntity.ok(AutomationRuleResponse.fromDomain(toggledRule));
    }

    @Override
    @PostMapping("/{id}/execute")
    public ResponseEntity<ActuationCommandResult> executeCommand(
            @PathVariable UUID id,
            @RequestBody(required = false) ExecuteActuationCommandRequest request
    ) {
        String payload = request != null ? request.commandPayload() : null;
        ActuationCommandResult result = executeActuationCommandUseCase.execute(
                new ExecuteActuationCommandRequest(payload).toInput(id)
        );
        return ResponseEntity.ok(result);
    }
}
