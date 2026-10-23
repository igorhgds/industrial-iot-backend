package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.EvaluateRuleGroupInput;
import henrique.igor.iiot.application.usecases.ruleGroup.dto.RuleEvaluationResult;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.RuleConditionRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluateRuleGroupUseCaseTest {

    @Mock
    private RuleGroupRepository ruleGroupRepository;

    @Mock
    private RuleConditionRepository ruleConditionRepository;

    @InjectMocks
    private EvaluateRuleGroupUseCase evaluateRuleGroupUseCase;

    @Test
    @DisplayName("Should trigger rule group with AND operator when all conditions are breached")
    void shouldTriggerWithAndOperatorWhenAllConditionsBreached() {
        UUID ruleGroupId = UUID.randomUUID();
        UUID tempSensorId = UUID.randomUUID();
        UUID vibSensorId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Critical Motor Failure", "Desc", equipment, Severity.CRITICAL, LogicalOperator.AND, true, OffsetDateTime.now());

        Sensor tempSensor = new Sensor(tempSensorId, "TEM-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment, OffsetDateTime.now());
        Sensor vibSensor = new Sensor(vibSensorId, "VIB-001", SensorType.VIBRATION, SensorStatus.ONLINE, "mm/s", "topic", equipment, OffsetDateTime.now());

        RuleCondition cond1 = new RuleCondition(ruleGroup, tempSensor, ComparisonOperator.GREATER_THAN, new BigDecimal("85.0"));
        RuleCondition cond2 = new RuleCondition(ruleGroup, vibSensor, ComparisonOperator.GREATER_THAN_OR_EQUAL, new BigDecimal("10.0"));

        EvaluateRuleGroupInput input = new EvaluateRuleGroupInput(
                ruleGroupId,
                Map.of(
                        tempSensorId, new BigDecimal("92.5"),
                        vibSensorId, new BigDecimal("10.0")
                )
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(ruleConditionRepository.findByRuleGroupId(ruleGroupId)).thenReturn(List.of(cond1, cond2));

        RuleEvaluationResult result = evaluateRuleGroupUseCase.execute(input);

        assertNotNull(result);
        assertTrue(result.triggered());
        assertEquals(Severity.CRITICAL, result.calculatedSeverity());
        assertEquals(2, result.breachedConditions().size());
        assertEquals(2, result.sensorSnapshots().size());
    }

    @Test
    @DisplayName("Should not trigger rule group with AND operator when only one condition is breached")
    void shouldNotTriggerWithAndOperatorWhenPartialBreach() {
        UUID ruleGroupId = UUID.randomUUID();
        UUID tempSensorId = UUID.randomUUID();
        UUID vibSensorId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Critical Motor Failure", "Desc", equipment, Severity.CRITICAL, LogicalOperator.AND, true, OffsetDateTime.now());

        Sensor tempSensor = new Sensor(tempSensorId, "TEM-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment, OffsetDateTime.now());
        Sensor vibSensor = new Sensor(vibSensorId, "VIB-001", SensorType.VIBRATION, SensorStatus.ONLINE, "mm/s", "topic", equipment, OffsetDateTime.now());

        RuleCondition cond1 = new RuleCondition(ruleGroup, tempSensor, ComparisonOperator.GREATER_THAN, new BigDecimal("85.0"));
        RuleCondition cond2 = new RuleCondition(ruleGroup, vibSensor, ComparisonOperator.GREATER_THAN, new BigDecimal("10.0"));

        EvaluateRuleGroupInput input = new EvaluateRuleGroupInput(
                ruleGroupId,
                Map.of(
                        tempSensorId, new BigDecimal("90.0"),
                        vibSensorId, new BigDecimal("5.0")
                )
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(ruleConditionRepository.findByRuleGroupId(ruleGroupId)).thenReturn(List.of(cond1, cond2));

        RuleEvaluationResult result = evaluateRuleGroupUseCase.execute(input);

        assertNotNull(result);
        assertFalse(result.triggered());
        assertEquals(Severity.REGULAR, result.calculatedSeverity());
        assertTrue(result.breachedConditions().isEmpty());
        assertTrue(result.sensorSnapshots().isEmpty());
    }

    @Test
    @DisplayName("Should trigger rule group with OR operator when at least one condition is breached and calculate severity automatically")
    void shouldTriggerWithOrOperatorAndCalculateDynamicSeverity() {
        UUID ruleGroupId = UUID.randomUUID();
        UUID tempSensorId = UUID.randomUUID();
        UUID pressureSensorId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Dynamic Alert", "Desc", equipment, Severity.REGULAR, LogicalOperator.OR, true, OffsetDateTime.now());

        Sensor tempSensor = new Sensor(tempSensorId, "TEM-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment, OffsetDateTime.now());
        Sensor pressureSensor = new Sensor(pressureSensorId, "PRS-001", SensorType.PRESSURE, SensorStatus.ONLINE, "bar", "topic", equipment, OffsetDateTime.now());

        RuleCondition cond1 = new RuleCondition(ruleGroup, tempSensor, ComparisonOperator.GREATER_THAN, new BigDecimal("100.0"));
        RuleCondition cond2 = new RuleCondition(ruleGroup, pressureSensor, ComparisonOperator.LESS_THAN, new BigDecimal("2.0"));

        EvaluateRuleGroupInput input = new EvaluateRuleGroupInput(
                ruleGroupId,
                Map.of(
                        tempSensorId, new BigDecimal("115.0"),
                        pressureSensorId, new BigDecimal("5.0")
                )
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(ruleConditionRepository.findByRuleGroupId(ruleGroupId)).thenReturn(List.of(cond1, cond2));

        RuleEvaluationResult result = evaluateRuleGroupUseCase.execute(input);

        assertNotNull(result);
        assertTrue(result.triggered());
        assertEquals(Severity.WARNING, result.calculatedSeverity());
        assertEquals(1, result.breachedConditions().size());
        assertEquals(2, result.sensorSnapshots().size());
    }

    @Test
    @DisplayName("Should return not triggered when rule group is inactive")
    void shouldReturnNotTriggeredWhenRuleGroupIsInactive() {
        UUID ruleGroupId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup inactiveRuleGroup = new RuleGroup(ruleGroupId, "Inactive Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, false, OffsetDateTime.now());

        EvaluateRuleGroupInput input = new EvaluateRuleGroupInput(
                ruleGroupId,
                Map.of(UUID.randomUUID(), BigDecimal.TEN)
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(inactiveRuleGroup));

        RuleEvaluationResult result = evaluateRuleGroupUseCase.execute(input);

        assertFalse(result.triggered());
        verify(ruleConditionRepository, never()).findByRuleGroupId(any());
    }

    @Test
    @DisplayName("Should return not triggered when rule group has no conditions")
    void shouldReturnNotTriggeredWhenNoConditionsExist() {
        UUID ruleGroupId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, true, OffsetDateTime.now());

        EvaluateRuleGroupInput input = new EvaluateRuleGroupInput(
                ruleGroupId,
                Map.of(UUID.randomUUID(), BigDecimal.TEN)
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(ruleConditionRepository.findByRuleGroupId(ruleGroupId)).thenReturn(Collections.emptyList());

        RuleEvaluationResult result = evaluateRuleGroupUseCase.execute(input);

        assertFalse(result.triggered());
    }

    @Test
    @DisplayName("Should throw exception when input or required fields are invalid")
    void shouldThrowExceptionWhenInputIsInvalid() {
        assertThrows(DomainValidationException.class, () -> evaluateRuleGroupUseCase.execute(null));
        assertThrows(DomainValidationException.class,
                () -> evaluateRuleGroupUseCase.execute(new EvaluateRuleGroupInput(null, Map.of(UUID.randomUUID(), BigDecimal.ONE))));
        assertThrows(DomainValidationException.class,
                () -> evaluateRuleGroupUseCase.execute(new EvaluateRuleGroupInput(UUID.randomUUID(), Collections.emptyMap())));
    }

    @Test
    @DisplayName("Should throw exception when rule group is not found")
    void shouldThrowExceptionWhenRuleGroupNotFound() {
        UUID ruleGroupId = UUID.randomUUID();
        EvaluateRuleGroupInput input = new EvaluateRuleGroupInput(ruleGroupId, Map.of(UUID.randomUUID(), BigDecimal.ONE));

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evaluateRuleGroupUseCase.execute(input));
    }
}
