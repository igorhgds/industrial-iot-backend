package henrique.igor.iiot.infrastructure.messaging.config;

import henrique.igor.iiot.application.usecases.telemetry.ProcessTelemetryIngestionUseCase;
import henrique.igor.iiot.domain.repositories.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ProcessTelemetryIngestionUseCase processTelemetryIngestionUseCase(
            EquipmentRepository equipmentRepository,
            GatewayRepository gatewayRepository,
            SensorRepository sensorRepository,
            TelemetryDataRepository telemetryDataRepository,
            EquipmentStateRepository equipmentStateRepository) {

        return new ProcessTelemetryIngestionUseCase(
                equipmentRepository,
                gatewayRepository,
                sensorRepository,
                telemetryDataRepository,
                equipmentStateRepository
        );
    }
}
