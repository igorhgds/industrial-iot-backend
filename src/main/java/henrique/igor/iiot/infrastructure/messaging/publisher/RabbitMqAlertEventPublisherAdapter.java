package henrique.igor.iiot.infrastructure.messaging.publisher;

import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.domain.ports.AlertEventPublisher;
import henrique.igor.iiot.infrastructure.messaging.dto.AlertSensorSnapshotEventDTO;
import henrique.igor.iiot.infrastructure.messaging.dto.AlertTriggeredEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class RabbitMqAlertEventPublisherAdapter implements AlertEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String eventsExchange;
    private final String alertRoutingKeyPrefix;

    public RabbitMqAlertEventPublisherAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.events:iiot.events.exchange}") String eventsExchange,
            @Value("${rabbitmq.routing-key.alert-prefix:alert.}") String alertRoutingKeyPrefix
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventsExchange = eventsExchange;
        this.alertRoutingKeyPrefix = alertRoutingKeyPrefix;
    }

    @Override
    public void publishAlertTriggered(Alert alert) {
        if (alert == null) {
            return;
        }

        List<AlertSensorSnapshotEventDTO> snapshots = alert.getSensorSnapshots() != null
                ? alert.getSensorSnapshots().stream()
                .map(s -> new AlertSensorSnapshotEventDTO(
                        s.getSensor() != null ? s.getSensor().getSensorId() : null,
                        s.getSensor() != null ? s.getSensor().getCode() : null,
                        s.getValueAtTrigger()
                ))
                .toList()
                : Collections.emptyList();

        Severity severity = alert.getSeverity() != null ? alert.getSeverity() : Severity.INFO;

        AlertTriggeredEventDTO payload = new AlertTriggeredEventDTO(
                alert.getAlertId(),
                alert.getEquipment() != null ? alert.getEquipment().getEquipmentId() : null,
                alert.getEquipment() != null ? alert.getEquipment().getEquipCode() : null,
                alert.getRuleGroup() != null ? alert.getRuleGroup().getRuleGroupId() : null,
                alert.getRuleGroup() != null ? alert.getRuleGroup().getName() : null,
                severity,
                alert.getMessage(),
                alert.getTriggeredAt(),
                snapshots
        );

        String routingKey = alertRoutingKeyPrefix + severity.name().toLowerCase(Locale.ROOT);

        log.info("Publishing AlertTriggeredEvent [alertId={}, severity={}, routingKey={}] to exchange {}",
                alert.getAlertId(), severity, routingKey, eventsExchange);

        rabbitTemplate.convertAndSend(eventsExchange, routingKey, payload);
    }
}
