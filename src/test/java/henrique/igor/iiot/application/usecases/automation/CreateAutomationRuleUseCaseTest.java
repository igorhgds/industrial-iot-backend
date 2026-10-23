package henrique.igor.iiot.application.usecases.automation;

import henrique.igor.iiot.application.usecases.automation.dto.CreateAutomationRuleInput;
import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.ActuatorRepository;
import henrique.igor.iiot.domain.repositories.AutomationRuleRepository;
import henrique.igor.iiot.domain.repositories.RuleConditionRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAutomationRuleUseCaseTest {

    @Mock
    private AutomationRuleRepository automationRuleRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private RuleConditionRepository ruleConditionRepository;

    @Mock
    private ActuatorRepository actuatorRepository;

    @InjectMocks
    private CreateAutomationRuleUseCase createAutomationRuleUseCase;

    @Test
    @DisplayName("Should create automation rule successfully")
    void shouldCreateAutomationRuleSuccessfully() {
        UUID sensorId = UUID.randomUUID();
        UUID conditionId = UUID.randomUUID();
        UUID actuatorId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(UUID.randomUUID(), "Overheating", "Desc", equipment, Severity.CRITICAL, LogicalOperator.AND, true, OffsetDateTime.now());
        Sensor sensor = new Sensor(sensorId, "TEM-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment, OffsetDateTime.now());
        RuleCondition condition = new RuleCondition(conditionId, ruleGroup, sensor, ComparisonOperator.GREATER_THAN, new BigDecimal("90.0"));
        Actuator actuator = new Actuator(actuatorId, "ACT-001", ActuatorType.RELAY, ActuatorStatus.ONLINE, "industry/actuators/ACT-001/cmd", "industry/actuators/ACT-001/state", equipment, OffsetDateTime.now());

        CreateAutomationRuleInput input = new CreateAutomationRuleInput(
                "Emergency Shutdown on Overheat",
                sensorId,
                conditionId,
                new BigDecimal("95.0"),
                ActionType.TOGGLE_ACTUATOR,
                actuatorId,
                true
        );

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        when(ruleConditionRepository.findById(conditionId)).thenReturn(Optional.of(condition));
        when(actuatorRepository.findById(actuatorId)).thenReturn(Optional.of(actuator));
        when(automationRuleRepository.save(any(AutomationRule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AutomationRule result = createAutomationRuleUseCase.execute(input);

        assertNotNull(result);
        assertEquals("Emergency Shutdown on Overheat", result.getName());
        assertEquals(sensor, result.getInputSensorId());
        assertEquals(condition, result.getCondition());
        assertEquals(new BigDecimal("95.0"), result.getThresholdValue());
        assertEquals(ActionType.TOGGLE_ACTUATOR, result.getActionType());
        assertEquals(actuator, result.getTargetActuator());
        assertTrue(result.getActive());

        verify(automationRuleRepository, times(1)).save(any(AutomationRule.class));
    }

    @Test
    @DisplayName("Should throw exception when condition does not belong to input sensor")
    void shouldThrowExceptionWhenConditionBelongsToDifferentSensor() {
        UUID sensorId1 = UUID.randomUUID();
        UUID sensorId2 = UUID.randomUUID();
        UUID conditionId = UUID.randomUUID();
        UUID actuatorId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(UUID.randomUUID(), "Overheating", "Desc", equipment, Severity.CRITICAL, LogicalOperator.AND, true, OffsetDateTime.now());
        Sensor sensor1 = new Sensor(sensorId1, "TEM-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment, OffsetDateTime.now());
        Sensor sensor2 = new Sensor(sensorId2, "VIB-001", SensorType.VIBRATION, SensorStatus.ONLINE, "mm/s", "topic", equipment, OffsetDateTime.now());
        RuleCondition conditionForSensor2 = new RuleCondition(conditionId, ruleGroup, sensor2, ComparisonOperator.GREATER_THAN, BigDecimal.TEN);
        Actuator actuator = new Actuator(actuatorId, "ACT-001", ActuatorType.RELAY, ActuatorStatus.ONLINE, "cmd", "state", equipment, OffsetDateTime.now());

        CreateAutomationRuleInput input = new CreateAutomationRuleInput(
                "Emergency Shutdown",
                sensorId1,
                conditionId,
                BigDecimal.TEN,
                ActionType.TOGGLE_ACTUATOR,
                actuatorId,
                true
        );

        when(sensorRepository.findById(sensorId1)).thenReturn(Optional.of(sensor1));
        when(ruleConditionRepository.findById(conditionId)).thenReturn(Optional.of(conditionForSensor2));
        when(actuatorRepository.findById(actuatorId)).thenReturn(Optional.of(actuator));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> createAutomationRuleUseCase.execute(input));

        assertEquals("RuleCondition does not belong to the specified input sensor.", ex.getMessage());
        verify(automationRuleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when required fields are null")
    void shouldThrowExceptionWhenFieldsAreNull() {
        UUID id = UUID.randomUUID();

        assertThrows(DomainValidationException.class, () -> createAutomationRuleUseCase.execute(null));
        assertThrows(DomainValidationException.class,
                () -> createAutomationRuleUseCase.execute(new CreateAutomationRuleInput(" ", id, id, BigDecimal.ONE, ActionType.TOGGLE_ACTUATOR, id, true)));
        assertThrows(DomainValidationException.class,
                () -> createAutomationRuleUseCase.execute(new CreateAutomationRuleInput("Name", null, id, BigDecimal.ONE, ActionType.TOGGLE_ACTUATOR, id, true)));
        assertThrows(DomainValidationException.class,
                () -> createAutomationRuleUseCase.execute(new CreateAutomationRuleInput("Name", id, null, BigDecimal.ONE, ActionType.TOGGLE_ACTUATOR, id, true)));
        assertThrows(DomainValidationException.class,
                () -> createAutomationRuleUseCase.execute(new CreateAutomationRuleInput("Name", id, id, null, ActionType.TOGGLE_ACTUATOR, id, true)));
        assertThrows(DomainValidationException.class,
                () -> createAutomationRuleUseCase.execute(new CreateAutomationRuleInput("Name", id, id, BigDecimal.ONE, null, id, true)));
        assertThrows(DomainValidationException.class,
                () -> createAutomationRuleUseCase.execute(new CreateAutomationRuleInput("Name", id, id, BigDecimal.ONE, ActionType.TOGGLE_ACTUATOR, null, true)));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when sensor is not found")
    void shouldThrowExceptionWhenSensorNotFound() {
        UUID sensorId = UUID.randomUUID();
        CreateAutomationRuleInput input = new CreateAutomationRuleInput(
                "Rule",
                sensorId,
                UUID.randomUUID(),
                BigDecimal.ONE,
                ActionType.TOGGLE_ACTUATOR,
                UUID.randomUUID(),
                false
        );

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> createAutomationRuleUseCase.execute(input));
    }
}
