package henrique.igor.iiot.application.usecases.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.AssignWorkOrderInput;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.UserRepository;
import henrique.igor.iiot.domain.repositories.WorkOrderRepository;
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
class AssignWorkOrderUseCaseTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssignWorkOrderUseCase assignWorkOrderUseCase;

    @Test
    @DisplayName("Should assign work order to technician successfully")
    void shouldAssignWorkOrderToTechnicianSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        UUID technicianId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        WorkOrder workOrder = new WorkOrder(workOrderId, null, equipment, null, "Fix Motor", "Desc", WorkOrderPriority.HIGH, WorkOrderStatus.OPEN, OffsetDateTime.now(), null);
        User technician = new User(technicianId, "Tech John", "john@iiot.com", "hash", "rec", UserRole.TECHNICIAN, null, OffsetDateTime.now());

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technician));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkOrder result = assignWorkOrderUseCase.execute(new AssignWorkOrderInput(workOrderId, technicianId));

        assertNotNull(result);
        assertEquals(technician, result.getAssignedTo());
        verify(workOrderRepository, times(1)).save(workOrder);
    }

    @Test
    @DisplayName("Should throw exception when assigning user without TECHNICIAN role")
    void shouldThrowExceptionWhenUserDoesNotHaveTechnicianRole() {
        UUID workOrderId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        WorkOrder workOrder = new WorkOrder(workOrderId, null, equipment, null, "Fix Motor", "Desc", WorkOrderPriority.HIGH, WorkOrderStatus.OPEN, OffsetDateTime.now(), null);
        User manager = new User(managerId, "Manager", "mgr@iiot.com", "hash", "rec", UserRole.MANAGER, null, OffsetDateTime.now());

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(userRepository.findById(managerId)).thenReturn(Optional.of(manager));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> assignWorkOrderUseCase.execute(new AssignWorkOrderInput(workOrderId, managerId)));

        assertEquals("Assigned user must have the TECHNICIAN role.", ex.getMessage());
        verify(workOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when assigning technician to a COMPLETED work order")
    void shouldThrowExceptionWhenWorkOrderIsClosed() {
        UUID workOrderId = UUID.randomUUID();
        UUID technicianId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        WorkOrder closedWorkOrder = new WorkOrder(workOrderId, null, equipment, null, "Fix Motor", "Desc", WorkOrderPriority.HIGH, WorkOrderStatus.COMPLETED, OffsetDateTime.now(), OffsetDateTime.now());
        User technician = new User(technicianId, "Tech John", "john@iiot.com", "hash", "rec", UserRole.TECHNICIAN, null, OffsetDateTime.now());

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(closedWorkOrder));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technician));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> assignWorkOrderUseCase.execute(new AssignWorkOrderInput(workOrderId, technicianId)));

        assertEquals("Cannot assign a technician to a closed WorkOrder.", ex.getMessage());
        verify(workOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when input or IDs are null")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(DomainValidationException.class, () -> assignWorkOrderUseCase.execute(null));
        assertThrows(DomainValidationException.class, () -> assignWorkOrderUseCase.execute(new AssignWorkOrderInput(null, UUID.randomUUID())));
        assertThrows(DomainValidationException.class, () -> assignWorkOrderUseCase.execute(new AssignWorkOrderInput(UUID.randomUUID(), null)));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when work order or user is not found")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> assignWorkOrderUseCase.execute(new AssignWorkOrderInput(workOrderId, UUID.randomUUID())));
    }
}
