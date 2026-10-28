package henrique.igor.iiot.application.usecases.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.UpdateWorkOrderStatusInput;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
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
class UpdateWorkOrderStatusUseCaseTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @InjectMocks
    private UpdateWorkOrderStatusUseCase updateWorkOrderStatusUseCase;

    @Test
    @DisplayName("Should transition work order from OPEN to IN_PROGRESS and then to COMPLETED")
    void shouldTransitionValidStatesSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        WorkOrder workOrder = new WorkOrder(workOrderId, null, equipment, null, "Fix Motor", "Desc", WorkOrderPriority.HIGH, WorkOrderStatus.OPEN, OffsetDateTime.now(), null);

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkOrder inProgress = updateWorkOrderStatusUseCase.execute(new UpdateWorkOrderStatusInput(workOrderId, WorkOrderStatus.IN_PROGRESS));
        assertEquals(WorkOrderStatus.IN_PROGRESS, inProgress.getStatus());
        assertNull(inProgress.getClosedAt());

        WorkOrder completed = updateWorkOrderStatusUseCase.execute(new UpdateWorkOrderStatusInput(workOrderId, WorkOrderStatus.COMPLETED));
        assertEquals(WorkOrderStatus.COMPLETED, completed.getStatus());
        assertNotNull(completed.getClosedAt());

        verify(workOrderRepository, times(2)).save(workOrder);
    }

    @Test
    @DisplayName("Should throw exception on invalid transition from COMPLETED to OPEN")
    void shouldThrowExceptionOnInvalidTransitionFromCompletedToOpen() {
        UUID workOrderId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        WorkOrder completedWorkOrder = new WorkOrder(workOrderId, null, equipment, null, "Fix Motor", "Desc", WorkOrderPriority.HIGH, WorkOrderStatus.COMPLETED, OffsetDateTime.now(), OffsetDateTime.now());

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(completedWorkOrder));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> updateWorkOrderStatusUseCase.execute(new UpdateWorkOrderStatusInput(workOrderId, WorkOrderStatus.OPEN)));

        assertEquals("Invalid WorkOrder status transition from COMPLETED to OPEN.", ex.getMessage());
        verify(workOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception on invalid direct transition from OPEN to COMPLETED")
    void shouldThrowExceptionOnInvalidDirectTransitionFromOpenToCompleted() {
        UUID workOrderId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        WorkOrder openWorkOrder = new WorkOrder(workOrderId, null, equipment, null, "Fix Motor", "Desc", WorkOrderPriority.HIGH, WorkOrderStatus.OPEN, OffsetDateTime.now(), null);

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(openWorkOrder));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> updateWorkOrderStatusUseCase.execute(new UpdateWorkOrderStatusInput(workOrderId, WorkOrderStatus.COMPLETED)));

        assertEquals("Invalid WorkOrder status transition from OPEN to COMPLETED.", ex.getMessage());
        verify(workOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when input or fields are null")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(DomainValidationException.class, () -> updateWorkOrderStatusUseCase.execute(null));
        assertThrows(DomainValidationException.class, () -> updateWorkOrderStatusUseCase.execute(new UpdateWorkOrderStatusInput(null, WorkOrderStatus.IN_PROGRESS)));
        assertThrows(DomainValidationException.class, () -> updateWorkOrderStatusUseCase.execute(new UpdateWorkOrderStatusInput(UUID.randomUUID(), null)));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when work order does not exist")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> updateWorkOrderStatusUseCase.execute(new UpdateWorkOrderStatusInput(workOrderId, WorkOrderStatus.IN_PROGRESS)));
    }
}
