package henrique.igor.iiot.infrastructure.messaging.consumer;

import henrique.igor.iiot.application.usecases.workOrder.CreateWorkOrderUseCase;
import henrique.igor.iiot.application.usecases.workOrder.dto.CreateWorkOrderInput;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.infrastructure.messaging.dto.AlertTriggeredEventDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriticalAlertAmqpConsumerTest {

    @Mock
    private CreateWorkOrderUseCase createWorkOrderUseCase;

    @InjectMocks
    private CriticalAlertAmqpConsumer consumer;

    @Test
    @DisplayName("Should consume critical alert event and create automated CRITICAL WorkOrder")
    void shouldConsumeCriticalAlertAndCreateWorkOrder() {
        UUID alertId = UUID.randomUUID();
        UUID equipmentId = UUID.randomUUID();
        UUID ruleGroupId = UUID.randomUUID();

        AlertTriggeredEventDTO event = new AlertTriggeredEventDTO(
                alertId,
                equipmentId,
                "MTR-USI-001",
                ruleGroupId,
                "Overheating Rule",
                Severity.CRITICAL,
                "Temperature above 95C",
                OffsetDateTime.now(),
                List.of()
        );

        consumer.handleCriticalAlert(event);

        ArgumentCaptor<CreateWorkOrderInput> captor = ArgumentCaptor.forClass(CreateWorkOrderInput.class);
        verify(createWorkOrderUseCase, times(1)).execute(captor.capture());

        CreateWorkOrderInput input = captor.getValue();
        assertNotNull(input);
        assertEquals("[AUTO] Critical Alert - Overheating Rule", input.title());
        assertEquals("Automated Work Order generated from critical alert: Temperature above 95C", input.description());
        assertEquals(WorkOrderPriority.CRITICAL, input.priority());
        assertEquals(equipmentId, input.equipmentId());
        assertEquals(alertId, input.alertId());
        assertNull(input.assignedToUserId());
    }

    @Test
    @DisplayName("Should ignore incomplete or null critical alert event")
    void shouldIgnoreIncompleteEvent() {
        consumer.handleCriticalAlert(null);
        consumer.handleCriticalAlert(new AlertTriggeredEventDTO(null, null, "MTR", null, "Title", Severity.CRITICAL, "Msg", OffsetDateTime.now(), List.of()));

        verifyNoInteractions(createWorkOrderUseCase);
    }
}
