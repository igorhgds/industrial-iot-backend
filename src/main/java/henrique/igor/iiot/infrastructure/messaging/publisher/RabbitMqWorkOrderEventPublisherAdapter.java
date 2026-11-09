package henrique.igor.iiot.infrastructure.messaging.publisher;

import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.domain.ports.WorkOrderEventPublisher;
import henrique.igor.iiot.infrastructure.messaging.dto.WorkOrderCreatedEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMqWorkOrderEventPublisherAdapter implements WorkOrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String eventsExchange;
    private final String workOrderCreatedRoutingKey;

    public RabbitMqWorkOrderEventPublisherAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.events:iiot.events.exchange}") String eventsExchange,
            @Value("${rabbitmq.routing-key.work-order-created:workorder.created}") String workOrderCreatedRoutingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventsExchange = eventsExchange;
        this.workOrderCreatedRoutingKey = workOrderCreatedRoutingKey;
    }

    @Override
    public void publishWorkOrderCreated(WorkOrder workOrder) {
        if (workOrder == null) {
            return;
        }

        WorkOrderCreatedEventDTO payload = new WorkOrderCreatedEventDTO(
                workOrder.getWorkOrderId(),
                workOrder.getEquipment() != null ? workOrder.getEquipment().getEquipmentId() : null,
                workOrder.getEquipment() != null ? workOrder.getEquipment().getEquipCode() : null,
                workOrder.getAlert() != null ? workOrder.getAlert().getAlertId() : null,
                workOrder.getAssignedTo() != null ? workOrder.getAssignedTo().getUserId() : null,
                workOrder.getTitle(),
                workOrder.getDescription(),
                workOrder.getPriority(),
                workOrder.getStatus(),
                workOrder.getCreatedAt()
        );

        log.info("Publishing WorkOrderCreatedEvent [workOrderId={}, priority={}, routingKey={}] to exchange {}",
                workOrder.getWorkOrderId(), workOrder.getPriority(), workOrderCreatedRoutingKey, eventsExchange);

        rabbitTemplate.convertAndSend(eventsExchange, workOrderCreatedRoutingKey, payload);
    }
}
