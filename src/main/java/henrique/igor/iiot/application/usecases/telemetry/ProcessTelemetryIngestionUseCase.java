package henrique.igor.iiot.application.usecases.telemetry;

import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.domain.entities.enums.HealthStatus;
import henrique.igor.iiot.domain.repositories.*;
import henrique.igor.iiot.infrastructure.messaging.dto.ReadingDTO;
import henrique.igor.iiot.infrastructure.messaging.dto.TelemetryPayloadDTO;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProcessTelemetryIngestionUseCase {

    private final EquipmentRepository equipmentRepository;
    private final GatewayRepository gatewayRepository;
    private final SensorRepository sensorRepository;
    private final TelemetryDataRepository telemetryDataRepository;
    private final EquipmentStateRepository equipmentStateRepository;

    public ProcessTelemetryIngestionUseCase(EquipmentRepository equipmentRepository, GatewayRepository gatewayRepository, SensorRepository sensorRepository, TelemetryDataRepository telemetryDataRepository, EquipmentStateRepository equipmentStateRepository) {
        this.equipmentRepository = equipmentRepository;
        this.gatewayRepository = gatewayRepository;
        this.sensorRepository = sensorRepository;
        this.telemetryDataRepository = telemetryDataRepository;
        this.equipmentStateRepository = equipmentStateRepository;
    }

    public void execute(TelemetryPayloadDTO payloadDTO, String rawPayload) {
        Equipment equipment = equipmentRepository.findByEquipCode(payloadDTO.equipCode())
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        Gateway gateway = gatewayRepository.findByCode(payloadDTO.gatewayCode())
                .orElseThrow(() -> new IllegalArgumentException("Gateway not found"));

        if(equipment.getGateway() == null || !equipment.getGateway().getGatewayCode().equals(gateway.getGatewayCode())){
            throw new IllegalArgumentException("Gateway does not belong to equipment");
        }

        OffsetDateTime timestamp = payloadDTO.timestamp() != null ? payloadDTO.timestamp() : OffsetDateTime.now();

        for (ReadingDTO reading : payloadDTO.readings()) {
            Sensor sensor = sensorRepository.findByCode(reading.sensorCode())
                    .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));

            TelemetryData telemetryData = new TelemetryData(
                    sensor,
                    timestamp,
                    reading.value(),
                    rawPayload
            );

            telemetryDataRepository.save(telemetryData);
        }

        Optional<EquipmentState> existingState = equipmentStateRepository.findByEquipmentId(equipment.getEquipmentId());

        HealthStatus status = existingState.isPresent()
                ? existingState.get().getStatus()
                : HealthStatus.HEALTH;

        EquipmentState equipmentState = new EquipmentState(
                equipment,
                status,
                rawPayload,
                timestamp
        );

        equipmentStateRepository.save(equipmentState);

    }
}
