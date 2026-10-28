package henrique.igor.iiot.application.usecases.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.CreateWorkOrderInput;
import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AlertRepository;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.UserRepository;
import henrique.igor.iiot.domain.repositories.WorkOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateWorkOrderUseCaseTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateWorkOrderUseCase createWorkOrderUseCase;

    @Test
    @DisplayName("Should create work order manually without alert or technician")
    void shouldCreateWorkOrderManuallySuccessfully() {
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());

        CreateWorkOrderInput input = new CreateWorkOrderInput(
                "Preventive Bearing Replacement",
                "Routine maintenance check",
                WorkOrderPriority.LOW,
                equipmentId,
                null,
                null
        );

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkOrder result = createWorkOrderUseCase.execute(input);

        assertNotNull(result);
        assertEquals("Preventive Bearing Replacement", result.getTitle());
        assertEquals("Routine maintenance check", result.getDescription());
        assertEquals(WorkOrderPriority.LOW, result.getPriority());
        assertEquals(WorkOrderStatus.OPEN, result.getStatus());
        assertEquals(equipment, result.getEquipment());
        assertNull(result.getAlert());
        assertNull(result.getAssignedTo());

        verify(workOrderRepository, times(1)).save(any(WorkOrder.class));
    }

    @Test
    @DisplayName("Should create work order from CRITICAL alert and infer CRITICAL priority automatically")
    void shouldCreateWorkOrderFromCriticalAlertWithTechnician() {
        UUID equipmentId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();
        UUID technicianId = UUID.randomUUID();

        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Alert criticalAlert = new Alert(alertId, equipment, null, Severity.CRITICAL, "Critical Overheat", AlertStatus.PENDING, null, OffsetDateTime.now(), null, List.of());
        User technician = new User(technicianId, "Tech Bob", "bob@iiot.com", "hash", "rec", UserRole.TECHNICIAN, null, OffsetDateTime.now());

        CreateWorkOrderInput input = new CreateWorkOrderInput(
                "Emergency Motor Overheat Inspection",
                "Generated from critical alert",
                null,
                equipmentId,
                alertId,
                technicianId
        );

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(criticalAlert));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technician));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkOrder result = createWorkOrderUseCase.execute(input);

        assertNotNull(result);
        assertEquals(WorkOrderPriority.CRITICAL, result.getPriority());
        assertEquals(criticalAlert, result.getAlert());
        assertEquals(technician, result.getAssignedTo());
        assertEquals(WorkOrderStatus.OPEN, result.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when assigned user is not a TECHNICIAN")
    void shouldThrowExceptionWhenAssignedUserIsNotTechnician() {
        UUID equipmentId = UUID.randomUUID();
        UUID operatorId = UUID.randomUUID();

        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        User operator = new User(operatorId, "Operator Alice", "alice@iiot.com", "hash", "rec", UserRole.OPERATOR, null, OffsetDateTime.now());

        CreateWorkOrderInput input = new CreateWorkOrderInput(
                "Inspection",
                "Desc",
                WorkOrderPriority.MEDIUM,
                equipmentId,
                null,
                operatorId
        );

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(userRepository.findById(operatorId)).thenReturn(Optional.of(operator));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> createWorkOrderUseCase.execute(input));

        assertEquals("Assigned user must have the TECHNICIAN role.", ex.getMessage());
        verify(workOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when alert does not belong to specified equipment")
    void shouldThrowExceptionWhenAlertBelongsToDifferentEquipment() {
        UUID equipmentId1 = UUID.randomUUID();
        UUID equipmentId2 = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();

        Equipment equipment1 = new Equipment(equipmentId1, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Equipment equipment2 = new Equipment(equipmentId2, "CMP-USI-002", EquipType.COMPRESSOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Alert alertFromOtherEquip = new Alert(alertId, equipment2, null, Severity.WARNING, "Alert", AlertStatus.PENDING, null, OffsetDateTime.now(), null, List.of());

        CreateWorkOrderInput input = new CreateWorkOrderInput(
                "Fix Motor",
                "Desc",
                WorkOrderPriority.HIGH,
                equipmentId1,
                alertId,
                null
        );

        when(equipmentRepository.findById(equipmentId1)).thenReturn(Optional.of(equipment1));
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alertFromOtherEquip));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> createWorkOrderUseCase.execute(input));

        assertEquals("Alert does not belong to the specified Equipment.", ex.getMessage());
        verify(workOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when required fields are missing")
    void shouldThrowExceptionWhenRequiredFieldsMissing() {
        UUID equipmentId = UUID.randomUUID();

        assertThrows(DomainValidationException.class, () -> createWorkOrderUseCase.execute(null));
        assertThrows(DomainValidationException.class,
                () -> createWorkOrderUseCase.execute(new CreateWorkOrderInput("Title", "Desc", null, null, null, null)));
        assertThrows(DomainValidationException.class,
                () -> createWorkOrderUseCase.execute(new CreateWorkOrderInput(" ", "Desc", null, equipmentId, null, null)));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when equipment is not found")
    void shouldThrowExceptionWhenEquipmentNotFound() {
        UUID equipmentId = UUID.randomUUID();
        CreateWorkOrderInput input = new CreateWorkOrderInput("Title", "Desc", null, equipmentId, null, null);

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> createWorkOrderUseCase.execute(input));
    }
}
