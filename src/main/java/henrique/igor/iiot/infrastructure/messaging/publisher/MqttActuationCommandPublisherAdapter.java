package henrique.igor.iiot.infrastructure.messaging.publisher;

import henrique.igor.iiot.domain.ports.ActuationCommandPublisher;
import org.springframework.stereotype.Component;

@Component
public class MqttActuationCommandPublisherAdapter implements ActuationCommandPublisher {

    @Override
    public void publishCommand(String topic, String payload) {
        // Outbound MQTT adapter hook for dispatching actuation commands to broker
    }
}
