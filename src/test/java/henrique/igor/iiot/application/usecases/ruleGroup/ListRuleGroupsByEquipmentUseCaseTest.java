package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.LogicalOperator;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListRuleGroupsByEquipmentUseCaseTest {

    @Mock
    private RuleGroupRepository ruleGroupRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private ListRuleGroupsByEquipmentUseCase listRuleGroupsByEquipmentUseCase;

    @Test
    @DisplayName("Should list rule groups by equipment ID successfully")
    void shouldListRuleGroupsByEquipmentSuccessfully() {
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());

        RuleGroup ruleGroup1 = new RuleGroup(UUID.randomUUID(), "Overheating Alert", "Desc 1", equipment, Severity.WARNING, LogicalOperator.AND, true, OffsetDateTime.now());
        RuleGroup ruleGroup2 = new RuleGroup(UUID.randomUUID(), "Critical Vibration", "Desc 2", equipment, Severity.CRITICAL, LogicalOperator.OR, false, OffsetDateTime.now());

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(ruleGroupRepository.findByEquipmentId(equipmentId)).thenReturn(List.of(ruleGroup1, ruleGroup2));

        List<RuleGroup> result = listRuleGroupsByEquipmentUseCase.execute(equipmentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Overheating Alert", result.get(0).getName());
        assertEquals("Critical Vibration", result.get(1).getName());

        verify(equipmentRepository, times(1)).findById(equipmentId);
        verify(ruleGroupRepository, times(1)).findByEquipmentId(equipmentId);
    }

    @Test
    @DisplayName("Should return empty list when equipment has no rule groups")
    void shouldReturnEmptyListWhenEquipmentHasNoRuleGroups() {
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(ruleGroupRepository.findByEquipmentId(equipmentId)).thenReturn(Collections.emptyList());

        List<RuleGroup> result = listRuleGroupsByEquipmentUseCase.execute(equipmentId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(equipmentRepository, times(1)).findById(equipmentId);
        verify(ruleGroupRepository, times(1)).findByEquipmentId(equipmentId);
    }

    @Test
    @DisplayName("Should throw exception when equipmentId is null")
    void shouldThrowExceptionWhenEquipmentIdIsNull() {
        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> listRuleGroupsByEquipmentUseCase.execute(null));

        assertEquals("Equipment ID cannot be null.", exception.getMessage());
        verify(equipmentRepository, never()).findById(any());
        verify(ruleGroupRepository, never()).findByEquipmentId(any());
    }

    @Test
    @DisplayName("Should throw exception when equipment is not found")
    void shouldThrowExceptionWhenEquipmentNotFound() {
        UUID equipmentId = UUID.randomUUID();
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> listRuleGroupsByEquipmentUseCase.execute(equipmentId));

        assertEquals("Equipment not found with identifier: " + equipmentId, exception.getMessage());
        verify(equipmentRepository, times(1)).findById(equipmentId);
        verify(ruleGroupRepository, never()).findByEquipmentId(any());
    }
}
