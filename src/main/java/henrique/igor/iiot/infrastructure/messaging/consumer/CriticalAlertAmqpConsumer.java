package henrique.igor.iiot.infrastructure.messaging.consumer;

import henrique.igor.iiot.application.usecases.workOrder.CreateWorkOrderUseCase;
import henrique.igor.iiot.application.usecases.workOrder.dto.CreateWorkOrderInput;
import henrique.igor.iiot.domain.entities.enums.WorkOrderPriority;
import henrique.igor.iiot.infrastructure.messaging.dto.AlertTriggeredEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CriticalAlertAmqpConsumer {

    private final CreateWorkOrderUseCase createWorkOrderUseCase;

    public CriticalAlertAmqpConsumer(CreateWorkOrderUseCase createWorkOrderUseCase) {
        this.createWorkOrderUseCase = createWorkOrderUseCase;
    }

    @RabbitListener(queues = "${rabbitmq.queue.critical-alerts:iiot.alerts.critical.queue}")
    public void handleCriticalAlert(AlertTriggeredEventDTO event) {
        if (event == null || event.equipmentId() == null || event.alertId() == null) {
            log.warn("Received invalid or incomplete CriticalAlertEvent payload: {}", event);
            return;
        }

        String ruleContext = (event.ruleGroupTitle() != null && !event.ruleGroupTitle().isBlank())
                ? event.ruleGroupTitle()
                : (event.equipmentCode() != null ? event.equipmentCode() : "Equipment Anomaly");

        String title = "[AUTO] Critical Alert - " + ruleContext;
        String description = event.message() != null
                ? "Automated Work Order generated from critical alert: " + event.message()
                : "Automated Work Order generated from critical alert.";

        log.info("Consuming critical alert event [alertId={}, equipmentCode={}] -> creating automated WorkOrder",
                event.alertId(), event.equipmentCode());

        createWorkOrderUseCase.execute(new CreateWorkOrderInput(
                title,
                description,
                WorkOrderPriority.CRITICAL,
                event.equipmentId(),
                event.alertId(),
                null
        ));
    }
}
