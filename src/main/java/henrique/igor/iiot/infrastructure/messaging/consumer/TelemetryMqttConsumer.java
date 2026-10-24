package henrique.igor.iiot.infrastructure.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import henrique.igor.iiot.application.usecases.telemetry.ProcessTelemetryIngestionUseCase;
import henrique.igor.iiot.application.usecases.telemetry.dto.ProcessTelemetryIngestionInput;
import henrique.igor.iiot.application.usecases.telemetry.dto.TelemetryReadingInput;
import henrique.igor.iiot.infrastructure.messaging.dto.TelemetryPayloadDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TelemetryMqttConsumer {

    private static final Logger log = LoggerFactory.getLogger(TelemetryMqttConsumer.class);

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

            List<TelemetryReadingInput> readings = payloadDTO.readings() != null
                    ? payloadDTO.readings().stream()
                    .map(r -> new TelemetryReadingInput(r.sensorCode(), r.value()))
                    .toList()
                    : List.of();

            ProcessTelemetryIngestionInput input = new ProcessTelemetryIngestionInput(
                    payloadDTO.equipCode(),
                    payloadDTO.gatewayCode(),
                    payloadDTO.timestamp(),
                    readings,
                    rawPayload
            );

            processTelemetryIngestionUseCase.execute(input);
        } catch (Exception e) {
            log.error("[MQTT CONSUMER ERROR] Failed to process telemetry message: {}", e.getMessage(), e);
        }
    }
}
