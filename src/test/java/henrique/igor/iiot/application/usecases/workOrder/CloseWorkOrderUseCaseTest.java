package henrique.igor.iiot.application.usecases.workOrder;

import henrique.igor.iiot.application.usecases.workOrder.dto.CloseWorkOrderInput;
import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AlertRepository;
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
class CloseWorkOrderUseCaseTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private CloseWorkOrderUseCase closeWorkOrderUseCase;

    @Test
    @DisplayName("Should close work order with resolution notes and resolve linked alert")
    void shouldCloseWorkOrderAndResolveLinkedAlertSuccessfully() {
        UUID workOrderId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Alert linkedAlert = new Alert(alertId, equipment, null, Severity.CRITICAL, "Overheating", AlertStatus.ACKNOWLEDGED, null, OffsetDateTime.now(), null, List.of());
        WorkOrder workOrder = new WorkOrder(workOrderId, linkedAlert, equipment, null, "Fix Motor", "Initial issue", WorkOrderPriority.CRITICAL, WorkOrderStatus.IN_PROGRESS, OffsetDateTime.now(), null);

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(alertRepository.findById(alertId)).thenReturn(Optional.of(linkedAlert));
        when(workOrderRepository.save(any(WorkOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkOrder result = closeWorkOrderUseCase.execute(new CloseWorkOrderInput(workOrderId, "Replaced cooling fan and tested motor."));

        assertNotNull(result);
        assertEquals(WorkOrderStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getClosedAt());
        assertTrue(result.getDescription().contains("Resolution: Replaced cooling fan and tested motor."));

        assertEquals(AlertStatus.RESOLVED, linkedAlert.getStatus());
        assertNotNull(linkedAlert.getResolvedAt());
        verify(alertRepository, times(1)).save(linkedAlert);
        verify(workOrderRepository, times(1)).save(workOrder);
    }

    @Test
    @DisplayName("Should throw exception when closing an already closed work order")
    void shouldThrowExceptionWhenWorkOrderAlreadyClosed() {
        UUID workOrderId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        WorkOrder closedWorkOrder = new WorkOrder(workOrderId, null, equipment, null, "Fix Motor", "Desc", WorkOrderPriority.HIGH, WorkOrderStatus.COMPLETED, OffsetDateTime.now(), OffsetDateTime.now());

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(closedWorkOrder));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> closeWorkOrderUseCase.execute(new CloseWorkOrderInput(workOrderId, "Done")));

        assertEquals("WorkOrder is already closed.", ex.getMessage());
        verify(workOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when resolution notes are blank")
    void shouldThrowExceptionWhenResolutionNotesAreBlank() {
        assertThrows(DomainValidationException.class, () -> closeWorkOrderUseCase.execute(null));
        assertThrows(DomainValidationException.class, () -> closeWorkOrderUseCase.execute(new CloseWorkOrderInput(null, "Notes")));
        assertThrows(DomainValidationException.class, () -> closeWorkOrderUseCase.execute(new CloseWorkOrderInput(UUID.randomUUID(), "   ")));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when work order does not exist")
    void shouldThrowExceptionWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> closeWorkOrderUseCase.execute(new CloseWorkOrderInput(workOrderId, "Resolved")));
    }
}
