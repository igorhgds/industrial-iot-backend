package henrique.igor.iiot.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import henrique.igor.iiot.application.usecases.telemetry.ProcessTelemetryIngestionUseCase;
import henrique.igor.iiot.infrastructure.messaging.dto.TelemetryPayloadDTO;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component
public class TelemetryMqttConsumer {

    private final ProcessTelemetryIngestionUseCase processTelemetryIngestionUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public TelemetryMqttConsumer(ProcessTelemetryIngestionUseCase processTelemetryIngestionUseCase) {
        this.processTelemetryIngestionUseCase = processTelemetryIngestionUseCase;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    @Transactional
    public void handleMessage(Message<String> message) {
        String rawPayload = message.getPayload();
        try {
            TelemetryPayloadDTO payloadDTO = objectMapper.readValue(rawPayload, TelemetryPayloadDTO.class);
            processTelemetryIngestionUseCase.execute(payloadDTO, rawPayload);
        } catch (Exception e) {
            System.err.println("[MQTT CONSUMER ERROR] Failed to process telemetry message: " + e.getMessage());
        }
    }
}