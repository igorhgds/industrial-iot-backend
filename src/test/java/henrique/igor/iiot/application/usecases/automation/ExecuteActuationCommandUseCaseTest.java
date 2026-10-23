package henrique.igor.iiot.application.usecases.automation;

import henrique.igor.iiot.application.usecases.automation.dto.ActuationCommandResult;
import henrique.igor.iiot.application.usecases.automation.dto.ExecuteActuationCommandInput;
import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.entities.AutomationRule;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.ports.ActuationCommandPublisher;
import henrique.igor.iiot.domain.repositories.AutomationRuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecuteActuationCommandUseCaseTest {

    @Mock
    private AutomationRuleRepository automationRuleRepository;

    @Mock
    private ActuationCommandPublisher commandPublisher;

    @InjectMocks
    private ExecuteActuationCommandUseCase executeActuationCommandUseCase;

    @Test
    @DisplayName("Should execute actuation command and publish to MQTT topic successfully")
    void shouldExecuteActuationCommandSuccessfully() {
        UUID automationId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Actuator actuator = new Actuator(UUID.randomUUID(), "REL-001-MTR-USI-001", ActuatorType.RELAY, ActuatorStatus.ONLINE, "industry/actuators/REL-001/cmd", "industry/actuators/REL-001/state", equipment, OffsetDateTime.now());
        AutomationRule rule = new AutomationRule(automationId, "Emergency Stop", null, null, new BigDecimal("95.0"), ActionType.TOGGLE_ACTUATOR, actuator, true);

        ExecuteActuationCommandInput input = new ExecuteActuationCommandInput(automationId, "{\"command\":\"SHUTDOWN\"}");

        when(automationRuleRepository.findById(automationId)).thenReturn(Optional.of(rule));

        ActuationCommandResult result = executeActuationCommandUseCase.execute(input);

        assertNotNull(result);
        assertEquals(automationId, result.automationId());
        assertEquals("REL-001-MTR-USI-001", result.actuatorCode());
        assertEquals("industry/actuators/REL-001/cmd", result.topic());
        assertEquals(ActionType.TOGGLE_ACTUATOR, result.actionType());
        assertEquals("{\"command\":\"SHUTDOWN\"}", result.payload());
        assertNotNull(result.executedAt());

        verify(commandPublisher, times(1)).publishCommand("industry/actuators/REL-001/cmd", "{\"command\":\"SHUTDOWN\"}");
    }

    @Test
    @DisplayName("Should throw exception when automation rule is inactive")
    void shouldThrowExceptionWhenAutomationRuleIsInactive() {
        UUID automationId = UUID.randomUUID();
        AutomationRule inactiveRule = new AutomationRule(automationId, "Emergency Stop", null, null, BigDecimal.TEN, ActionType.TOGGLE_ACTUATOR, null, false);

        when(automationRuleRepository.findById(automationId)).thenReturn(Optional.of(inactiveRule));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> executeActuationCommandUseCase.execute(new ExecuteActuationCommandInput(automationId, "STOP")));

        assertEquals("Cannot execute actuation command for an inactive AutomationRule.", ex.getMessage());
        verify(commandPublisher, never()).publishCommand(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when target actuator is OFFLINE")
    void shouldThrowExceptionWhenActuatorIsOffline() {
        UUID automationId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Actuator offlineActuator = new Actuator(UUID.randomUUID(), "REL-001", ActuatorType.RELAY, ActuatorStatus.OFFLINE, "industry/actuators/REL-001/cmd", "state", equipment, OffsetDateTime.now());
        AutomationRule rule = new AutomationRule(automationId, "Emergency Stop", null, null, BigDecimal.TEN, ActionType.TOGGLE_ACTUATOR, offlineActuator, true);

        when(automationRuleRepository.findById(automationId)).thenReturn(Optional.of(rule));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> executeActuationCommandUseCase.execute(new ExecuteActuationCommandInput(automationId, "STOP")));

        assertEquals("Target actuator must be ONLINE to receive commands.", ex.getMessage());
        verify(commandPublisher, never()).publishCommand(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when input or automationRuleId is null")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(DomainValidationException.class, () -> executeActuationCommandUseCase.execute(null));
        assertThrows(DomainValidationException.class, () -> executeActuationCommandUseCase.execute(new ExecuteActuationCommandInput(null, "STOP")));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when automation rule is not found")
    void shouldThrowExceptionWhenRuleNotFound() {
        UUID automationId = UUID.randomUUID();
        when(automationRuleRepository.findById(automationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> executeActuationCommandUseCase.execute(new ExecuteActuationCommandInput(automationId, "STOP")));
    }
}
