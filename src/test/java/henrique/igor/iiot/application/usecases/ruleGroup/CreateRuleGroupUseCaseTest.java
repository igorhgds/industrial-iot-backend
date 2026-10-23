package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.CreateRuleGroupInput;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.LogicalOperator;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceAlreadyExistsException;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRuleGroupUseCaseTest {

    @Mock
    private RuleGroupRepository ruleGroupRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private CreateRuleGroupUseCase createRuleGroupUseCase;

    @Test
    @DisplayName("Should create rule group successfully")
    void shouldCreateRuleGroupSuccessfully() {
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());

        CreateRuleGroupInput request = new CreateRuleGroupInput(
                "Overheating Alert",
                "Triggers when motor temperature exceeds safe limits",
                equipmentId,
                LogicalOperator.AND,
                false
        );

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(ruleGroupRepository.findByNameAndEquipmentId("Overheating Alert", equipmentId)).thenReturn(Optional.empty());
        when(ruleGroupRepository.save(any(RuleGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RuleGroup result = createRuleGroupUseCase.execute(request);

        assertNotNull(result);
        assertEquals("Overheating Alert", result.getName());
        assertEquals("Triggers when motor temperature exceeds safe limits", result.getDescription());
        assertEquals(equipment, result.getEquipment());
        assertEquals(LogicalOperator.AND, result.getOperatorType());
        assertEquals(Severity.REGULAR, result.getSeverity());
        assertFalse(result.getActive());

        verify(equipmentRepository, times(1)).findById(equipmentId);
        verify(ruleGroupRepository, times(1)).findByNameAndEquipmentId("Overheating Alert", equipmentId);
        verify(ruleGroupRepository, times(1)).save(any(RuleGroup.class));
    }

    @Test
    @DisplayName("Should throw exception when equipment ID is null")
    void shouldThrowExceptionWhenEquipmentIdIsNull() {
        CreateRuleGroupInput request = new CreateRuleGroupInput(
                "Overheating Alert",
                "Description",
                null,
                LogicalOperator.AND,
                false
        );

        DomainValidationException exception = assertThrows(DomainValidationException.class,
                () -> createRuleGroupUseCase.execute(request));

        assertEquals("Equipment ID cannot be null.", exception.getMessage());
        verify(equipmentRepository, never()).findById(any());
        verify(ruleGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when equipment is not found")
    void shouldThrowExceptionWhenEquipmentNotFound() {
        UUID equipmentId = UUID.randomUUID();
        CreateRuleGroupInput request = new CreateRuleGroupInput(
                "Overheating Alert",
                "Description",
                equipmentId,
                LogicalOperator.AND,
                false
        );

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> createRuleGroupUseCase.execute(request));

        assertEquals("Equipment not found with identifier: " + equipmentId, exception.getMessage());
        verify(equipmentRepository, times(1)).findById(equipmentId);
        verify(ruleGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when rule group name already exists for equipment")
    void shouldThrowExceptionWhenRuleGroupNameAlreadyExists() {
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup existingRuleGroup = new RuleGroup("Overheating Alert", "Existing description", equipment, LogicalOperator.AND);

        CreateRuleGroupInput request = new CreateRuleGroupInput(
                "Overheating Alert",
                "New description",
                equipmentId,
                LogicalOperator.OR,
                false
        );

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(ruleGroupRepository.findByNameAndEquipmentId("Overheating Alert", equipmentId)).thenReturn(Optional.of(existingRuleGroup));

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> createRuleGroupUseCase.execute(request));

        assertEquals("RuleGroup already exists with name: 'Overheating Alert'", exception.getMessage());
        verify(equipmentRepository, times(1)).findById(equipmentId);
        verify(ruleGroupRepository, times(1)).findByNameAndEquipmentId("Overheating Alert", equipmentId);
        verify(ruleGroupRepository, never()).save(any());
    }
}
