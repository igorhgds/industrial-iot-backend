package henrique.igor.iiot.application.usecases.ruleGroup;

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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateRuleGroupUseCaseTest {

    @Mock
    private RuleGroupRepository ruleGroupRepository;

    @Mock
    private RuleConditionRepository ruleConditionRepository;

    @InjectMocks
    private ActivateRuleGroupUseCase activateRuleGroupUseCase;

    @Test
    @DisplayName("Should activate rule group successfully when it has conditions")
    void shouldActivateRuleGroupSuccessfully() {
        UUID ruleGroupId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Overheating Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, false, OffsetDateTime.now());
        Sensor sensor = new Sensor("TEM-001", SensorType.TEMPERATURE, "°C", "topic", equipment);
        RuleCondition condition = new RuleCondition(ruleGroup, sensor, ComparisonOperator.GREATER_THAN, BigDecimal.valueOf(80.0));

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(ruleConditionRepository.findByRuleGroupId(ruleGroupId)).thenReturn(List.of(condition));
        when(ruleGroupRepository.save(any(RuleGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RuleGroup result = activateRuleGroupUseCase.execute(ruleGroupId);

        assertNotNull(result);
        assertTrue(result.getActive());
        assertEquals(ruleGroupId, result.getRuleGroupId());

        verify(ruleGroupRepository, times(1)).findById(ruleGroupId);
        verify(ruleConditionRepository, times(1)).findByRuleGroupId(ruleGroupId);
        verify(ruleGroupRepository, times(1)).save(ruleGroup);
    }

    @Test
    @DisplayName("Should throw exception when ruleGroupId is null")
    void shouldThrowExceptionWhenRuleGroupIdIsNull() {
        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> activateRuleGroupUseCase.execute(null));

        assertEquals("RuleGroup ID cannot be null.", exception.getMessage());
        verify(ruleGroupRepository, never()).findById(any());
        verify(ruleConditionRepository, never()).findByRuleGroupId(any());
        verify(ruleGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when rule group is not found")
    void shouldThrowExceptionWhenRuleGroupNotFound() {
        UUID ruleGroupId = UUID.randomUUID();
        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> activateRuleGroupUseCase.execute(ruleGroupId));

        assertEquals("RuleGroup not found with identifier: " + ruleGroupId, exception.getMessage());
        verify(ruleGroupRepository, times(1)).findById(ruleGroupId);
        verify(ruleConditionRepository, never()).findByRuleGroupId(any());
        verify(ruleGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when activating rule group without conditions")
    void shouldThrowExceptionWhenNoConditionsExist() {
        UUID ruleGroupId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Overheating Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, false, OffsetDateTime.now());

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(ruleConditionRepository.findByRuleGroupId(ruleGroupId)).thenReturn(Collections.emptyList());

        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> activateRuleGroupUseCase.execute(ruleGroupId));

        assertEquals("Cannot activate a RuleGroup without at least one condition.", exception.getMessage());
        verify(ruleGroupRepository, times(1)).findById(ruleGroupId);
        verify(ruleConditionRepository, times(1)).findByRuleGroupId(ruleGroupId);
        verify(ruleGroupRepository, never()).save(any());
    }
}
