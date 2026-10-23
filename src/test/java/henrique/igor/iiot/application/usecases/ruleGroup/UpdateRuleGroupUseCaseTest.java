package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.UpdateRuleGroupInput;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceAlreadyExistsException;
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
class UpdateRuleGroupUseCaseTest {

    @Mock
    private RuleGroupRepository ruleGroupRepository;

    @Mock
    private RuleConditionRepository ruleConditionRepository;

    @InjectMocks
    private UpdateRuleGroupUseCase updateRuleGroupUseCase;

    @Test
    @DisplayName("Should update rule group info and activate successfully when conditions exist")
    void shouldUpdateRuleGroupInfoAndActivateSuccessfully() {
        UUID ruleGroupId = UUID.randomUUID();
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup existingRuleGroup = new RuleGroup(ruleGroupId, "Old Alert", "Old Desc", equipment, Severity.REGULAR, LogicalOperator.AND, false, OffsetDateTime.now());
        Sensor sensor = new Sensor("TEM-001", SensorType.TEMPERATURE, "°C", "topic", equipment);
        RuleCondition condition = new RuleCondition(existingRuleGroup, sensor, ComparisonOperator.GREATER_THAN, BigDecimal.valueOf(90.0));

        UpdateRuleGroupInput request = new UpdateRuleGroupInput(
                ruleGroupId,
                "Critical Overheating Alert",
                "New Description",
                Severity.CRITICAL,
                LogicalOperator.OR,
                true
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(existingRuleGroup));
        when(ruleGroupRepository.findByNameAndEquipmentId("Critical Overheating Alert", equipmentId)).thenReturn(Optional.empty());
        when(ruleConditionRepository.findByRuleGroupId(ruleGroupId)).thenReturn(List.of(condition));
        when(ruleGroupRepository.save(any(RuleGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RuleGroup result = updateRuleGroupUseCase.execute(request);

        assertNotNull(result);
        assertEquals("Critical Overheating Alert", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(Severity.CRITICAL, result.getSeverity());
        assertEquals(LogicalOperator.OR, result.getOperatorType());
        assertTrue(result.getActive());

        verify(ruleGroupRepository, times(1)).findById(ruleGroupId);
        verify(ruleGroupRepository, times(1)).findByNameAndEquipmentId("Critical Overheating Alert", equipmentId);
        verify(ruleConditionRepository, times(1)).findByRuleGroupId(ruleGroupId);
        verify(ruleGroupRepository, times(1)).save(existingRuleGroup);
    }

    @Test
    @DisplayName("Should deactivate rule group when isActive is false")
    void shouldDeactivateRuleGroupWhenIsActiveIsFalse() {
        UUID ruleGroupId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup existingRuleGroup = new RuleGroup(ruleGroupId, "Overheating Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, true, OffsetDateTime.now());

        UpdateRuleGroupInput request = new UpdateRuleGroupInput(
                ruleGroupId,
                null,
                null,
                null,
                null,
                false
        );

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(existingRuleGroup));
        when(ruleGroupRepository.save(any(RuleGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RuleGroup result = updateRuleGroupUseCase.execute(request);

        assertNotNull(result);
        assertFalse(result.getActive());
        assertEquals("Overheating Alert", result.getName());

        verify(ruleConditionRepository, never()).findByRuleGroupId(any());
        verify(ruleGroupRepository, times(1)).save(existingRuleGroup);
    }

    @Test
    @DisplayName("Should throw exception when input or ruleGroupId is null")
    void shouldThrowExceptionWhenInputOrRuleGroupIdIsNull() {
        UpdateRuleGroupInput requestWithNullId = new UpdateRuleGroupInput(null, "Name", "Desc", Severity.INFO, LogicalOperator.AND, false);

        DomainValidationException exceptionNullInput = assertThrows(DomainValidationException.class,
                () -> updateRuleGroupUseCase.execute(null));
        DomainValidationException exceptionNullId = assertThrows(DomainValidationException.class,
                () -> updateRuleGroupUseCase.execute(requestWithNullId));

        assertEquals("RuleGroup ID cannot be null.", exceptionNullInput.getMessage());
        assertEquals("RuleGroup ID cannot be null.", exceptionNullId.getMessage());
        verify(ruleGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when rule group is not found")
    void shouldThrowExceptionWhenRuleGroupNotFound() {
        UUID ruleGroupId = UUID.randomUUID();
        UpdateRuleGroupInput request = new UpdateRuleGroupInput(ruleGroupId, "New Name", null, null, null, null);

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> updateRuleGroupUseCase.execute(request));

        assertEquals("RuleGroup not found with identifier: " + ruleGroupId, exception.getMessage());
        verify(ruleGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when new name already exists for the same equipment")
    void shouldThrowExceptionWhenNewNameAlreadyExistsForSameEquipment() {
        UUID ruleGroupId = UUID.randomUUID();
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup existingRuleGroup = new RuleGroup(ruleGroupId, "Old Name", "Desc", equipment, Severity.REGULAR, LogicalOperator.AND, false, OffsetDateTime.now());
        RuleGroup anotherRuleGroup = new RuleGroup(UUID.randomUUID(), "Duplicate Name", "Desc", equipment, Severity.REGULAR, LogicalOperator.AND, false, OffsetDateTime.now());

        UpdateRuleGroupInput request = new UpdateRuleGroupInput(ruleGroupId, "Duplicate Name", null, null, null, null);

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(existingRuleGroup));
        when(ruleGroupRepository.findByNameAndEquipmentId("Duplicate Name", equipmentId)).thenReturn(Optional.of(anotherRuleGroup));

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> updateRuleGroupUseCase.execute(request));

        assertEquals("RuleGroup already exists with name: 'Duplicate Name'", exception.getMessage());
        verify(ruleGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when activating rule group without conditions")
    void shouldThrowExceptionWhenActivatingWithoutConditions() {
        UUID ruleGroupId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup existingRuleGroup = new RuleGroup(ruleGroupId, "Overheating Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, false, OffsetDateTime.now());

        UpdateRuleGroupInput request = new UpdateRuleGroupInput(ruleGroupId, null, null, null, null, true);

        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(existingRuleGroup));
        when(ruleConditionRepository.findByRuleGroupId(ruleGroupId)).thenReturn(Collections.emptyList());

        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> updateRuleGroupUseCase.execute(request));

        assertEquals("Cannot activate a RuleGroup without at least one condition.", exception.getMessage());
        verify(ruleGroupRepository, never()).save(any());
    }
}
