package henrique.igor.iiot.infrastructure.messaging.publisher;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.domain.entities.enums.WorkOrderStatus;
import henrique.igor.iiot.infrastructure.messaging.dto.WorkOrderCreatedEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMqWorkOrderEventPublisherAdapterTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMqWorkOrderEventPublisherAdapter publisherAdapter;

    @BeforeEach
    void setUp() {
        publisherAdapter = new RabbitMqWorkOrderEventPublisherAdapter(
                rabbitTemplate,
                "iiot.events.exchange",
                "workorder.created"
        );
    }

    @Test
    @DisplayName("Should publish WorkOrderCreatedEvent with workorder.created routing key")
    void shouldPublishWorkOrderCreatedEvent() {
        UUID workOrderId = UUID.randomUUID();
        UUID equipmentId = UUID.randomUUID();

        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        WorkOrder workOrder = new WorkOrder(
                workOrderId,
                null,
                equipment,
                null,
                "Emergency Inspection",
                "Check bearings",
                WorkOrderPriority.CRITICAL,
                WorkOrderStatus.OPEN,
                OffsetDateTime.now(),
                null
        );

        publisherAdapter.publishWorkOrderCreated(workOrder);

        ArgumentCaptor<WorkOrderCreatedEventDTO> captor = ArgumentCaptor.forClass(WorkOrderCreatedEventDTO.class);
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("iiot.events.exchange"), eq("workorder.created"), captor.capture());

        WorkOrderCreatedEventDTO event = captor.getValue();
        assertNotNull(event);
        assertEquals(workOrderId, event.workOrderId());
        assertEquals(equipmentId, event.equipmentId());
        assertEquals("MTR-USI-001", event.equipmentCode());
        assertEquals("Emergency Inspection", event.title());
        assertEquals(WorkOrderPriority.CRITICAL, event.priority());
        assertEquals(WorkOrderStatus.OPEN, event.status());
    }

    @Test
    @DisplayName("Should not publish when work order is null")
    void shouldNotPublishWhenWorkOrderIsNull() {
        publisherAdapter.publishWorkOrderCreated(null);
        verifyNoInteractions(rabbitTemplate);
    }
}
