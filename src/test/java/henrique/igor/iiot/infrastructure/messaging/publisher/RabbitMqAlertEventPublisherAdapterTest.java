package henrique.igor.iiot.infrastructure.messaging.publisher;

import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.AlertSensorSnapshot;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.LogicalOperator;
import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.entities.enums.SensorType;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.infrastructure.messaging.dto.AlertTriggeredEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMqAlertEventPublisherAdapterTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMqAlertEventPublisherAdapter publisherAdapter;

    @BeforeEach
    void setUp() {
        publisherAdapter = new RabbitMqAlertEventPublisherAdapter(
                rabbitTemplate,
                "iiot.events.exchange",
                "alert."
        );
    }

    @Test
    @DisplayName("Should publish CRITICAL alert event with alert.critical routing key")
    void shouldPublishCriticalAlertEvent() {
        UUID alertId = UUID.randomUUID();
        UUID equipmentId = UUID.randomUUID();
        UUID ruleGroupId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();

        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Overheating Rule", "Desc", equipment, Severity.CRITICAL, LogicalOperator.AND, true, OffsetDateTime.now());
        Sensor sensor = new Sensor(sensorId, "TEM-001-MTR-USI-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "C", "topic", equipment, OffsetDateTime.now());
        AlertSensorSnapshot snapshot = new AlertSensorSnapshot(sensor, new BigDecimal("98.5"));

        Alert alert = new Alert(
                alertId,
                equipment,
                ruleGroup,
                Severity.CRITICAL,
                "Motor critical overheating",
                AlertStatus.PENDING,
                null,
                OffsetDateTime.now(),
                null,
                List.of(snapshot)
        );

        publisherAdapter.publishAlertTriggered(alert);

        ArgumentCaptor<AlertTriggeredEventDTO> captor = ArgumentCaptor.forClass(AlertTriggeredEventDTO.class);
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("iiot.events.exchange"), eq("alert.critical"), captor.capture());

        AlertTriggeredEventDTO event = captor.getValue();
        assertNotNull(event);
        assertEquals(alertId, event.alertId());
        assertEquals(equipmentId, event.equipmentId());
        assertEquals("MTR-USI-001", event.equipmentCode());
        assertEquals(ruleGroupId, event.ruleGroupId());
        assertEquals("Overheating Rule", event.ruleGroupTitle());
        assertEquals(Severity.CRITICAL, event.severity());
        assertEquals("Motor critical overheating", event.message());
        assertEquals(1, event.sensorSnapshots().size());
        assertEquals(sensorId, event.sensorSnapshots().get(0).sensorId());
        assertEquals(new BigDecimal("98.5"), event.sensorSnapshots().get(0).valueAtTrigger());
    }

    @Test
    @DisplayName("Should not publish when alert is null")
    void shouldNotPublishWhenAlertIsNull() {
        publisherAdapter.publishAlertTriggered(null);
        verifyNoInteractions(rabbitTemplate);
    }
}
