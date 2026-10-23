package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.AddConditionToRuleGroupInput;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.RuleConditionRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;
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
class AddConditionToRuleGroupUseCaseTest {

    @Mock
    private RuleGroupRepository ruleGroupRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private RuleConditionRepository ruleConditionRepository;

    @InjectMocks
    private AddConditionToRuleGroupUseCase addConditionToRuleGroupUseCase;

    @Test
    @DisplayName("Should add condition to rule group successfully")
    void shouldAddConditionToRuleGroupSuccessfully() {
        UUID equipmentId = UUID.randomUUID();
        UUID ruleGroupId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();

        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Overheating Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, false, OffsetDateTime.now());
        Sensor sensor = new Sensor(sensorId, "TEM-001-MTR-USI-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment, OffsetDateTime.now());

        AddConditionToRuleGroupInput request = new AddConditionToRuleGroupInput(
                ruleGroupId,
                sensorId,
                ComparisonOperator.GREATER_THAN,
                BigDecimal.valueOf(85.5)
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        when(ruleConditionRepository.save(any(RuleCondition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RuleCondition result = addConditionToRuleGroupUseCase.execute(request);

        assertNotNull(result);
        assertEquals(ruleGroup, result.getRuleGroup());
        assertEquals(sensor, result.getSensor());
        assertEquals(ComparisonOperator.GREATER_THAN, result.getOperator());
        assertEquals(BigDecimal.valueOf(85.5), result.getThresholdValue());

        verify(ruleGroupRepository, times(1)).findById(ruleGroupId);
        verify(sensorRepository, times(1)).findById(sensorId);
        verify(ruleConditionRepository, times(1)).save(any(RuleCondition.class));
    }

    @Test
    @DisplayName("Should throw exception when input payload is null")
    void shouldThrowExceptionWhenInputIsNull() {
        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> addConditionToRuleGroupUseCase.execute(null));

        assertEquals("Input payload cannot be null.", exception.getMessage());
        verify(ruleConditionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when ruleGroupId is null")
    void shouldThrowExceptionWhenRuleGroupIdIsNull() {
        AddConditionToRuleGroupInput request = new AddConditionToRuleGroupInput(
                null,
                UUID.randomUUID(),
                ComparisonOperator.GREATER_THAN,
                BigDecimal.TEN
        );

        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> addConditionToRuleGroupUseCase.execute(request));

        assertEquals("RuleGroup ID cannot be null.", exception.getMessage());
        verify(ruleConditionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when sensorId is null")
    void shouldThrowExceptionWhenSensorIdIsNull() {
        AddConditionToRuleGroupInput request = new AddConditionToRuleGroupInput(
                UUID.randomUUID(),
                null,
                ComparisonOperator.GREATER_THAN,
                BigDecimal.TEN
        );

        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> addConditionToRuleGroupUseCase.execute(request));

        assertEquals("Sensor ID cannot be null.", exception.getMessage());
        verify(ruleConditionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when operator is null")
    void shouldThrowExceptionWhenOperatorIsNull() {
        AddConditionToRuleGroupInput request = new AddConditionToRuleGroupInput(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                BigDecimal.TEN
        );

        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> addConditionToRuleGroupUseCase.execute(request));

        assertEquals("Comparison operator cannot be null.", exception.getMessage());
        verify(ruleConditionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when threshold value is null")
    void shouldThrowExceptionWhenThresholdValueIsNull() {
        AddConditionToRuleGroupInput request = new AddConditionToRuleGroupInput(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ComparisonOperator.GREATER_THAN,
                null
        );

        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> addConditionToRuleGroupUseCase.execute(request));

        assertEquals("Threshold value cannot be null.", exception.getMessage());
        verify(ruleConditionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when rule group is not found")
    void shouldThrowExceptionWhenRuleGroupNotFound() {
        UUID ruleGroupId = UUID.randomUUID();
        AddConditionToRuleGroupInput request = new AddConditionToRuleGroupInput(
                ruleGroupId,
                UUID.randomUUID(),
                ComparisonOperator.GREATER_THAN,
                BigDecimal.TEN
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> addConditionToRuleGroupUseCase.execute(request));

        assertEquals("RuleGroup not found with identifier: " + ruleGroupId, exception.getMessage());
        verify(ruleConditionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when sensor is not found")
    void shouldThrowExceptionWhenSensorNotFound() {
        UUID ruleGroupId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Overheating Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, false, OffsetDateTime.now());

        AddConditionToRuleGroupInput request = new AddConditionToRuleGroupInput(
                ruleGroupId,
                sensorId,
                ComparisonOperator.GREATER_THAN,
                BigDecimal.TEN
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> addConditionToRuleGroupUseCase.execute(request));

        assertEquals("Sensor not found with identifier: " + sensorId, exception.getMessage());
        verify(ruleConditionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when sensor does not belong to the same equipment as rule group")
    void shouldThrowExceptionWhenSensorBelongsToDifferentEquipment() {
        UUID ruleGroupId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();

        Equipment equipment1 = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Equipment equipment2 = new Equipment(UUID.randomUUID(), "CMP-USI-002", EquipType.COMPRESSOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());

        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Overheating Alert", "Desc", equipment1, Severity.WARNING, LogicalOperator.AND, false, OffsetDateTime.now());
        Sensor sensor = new Sensor(sensorId, "TEM-001-CMP-USI-002", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment2, OffsetDateTime.now());

        AddConditionToRuleGroupInput request = new AddConditionToRuleGroupInput(
                ruleGroupId,
                sensorId,
                ComparisonOperator.GREATER_THAN,
                BigDecimal.TEN
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));

        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> addConditionToRuleGroupUseCase.execute(request));

        assertEquals("Sensor does not belong to the same equipment as the RuleGroup.", exception.getMessage());
        verify(ruleConditionRepository, never()).save(any());
    }
}
