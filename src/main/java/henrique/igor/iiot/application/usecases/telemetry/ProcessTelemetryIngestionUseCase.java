package henrique.igor.iiot.application.usecases.telemetry;

import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.domain.entities.enums.HealthStatus;
import henrique.igor.iiot.domain.exceptions.GatewayMismatchException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.*;
import henrique.igor.iiot.infrastructure.messaging.dto.ReadingDTO;
import henrique.igor.iiot.infrastructure.messaging.dto.TelemetryPayloadDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProcessTelemetryIngestionUseCase {

    private final EquipmentRepository equipmentRepository;
    private final GatewayRepository gatewayRepository;
    private final SensorRepository sensorRepository;
    private final TelemetryDataRepository telemetryDataRepository;
    private final EquipmentStateRepository equipmentStateRepository;
    private final ProcessEquipmentAnomaliesUseCase processEquipmentAnomaliesUseCase;

    public ProcessTelemetryIngestionUseCase(
            EquipmentRepository equipmentRepository,
            GatewayRepository gatewayRepository,
            SensorRepository sensorRepository,
            TelemetryDataRepository telemetryDataRepository,
            EquipmentStateRepository equipmentStateRepository,
            ProcessEquipmentAnomaliesUseCase processEquipmentAnomaliesUseCase
    ) {
        this.equipmentRepository = equipmentRepository;
        this.gatewayRepository = gatewayRepository;
        this.sensorRepository = sensorRepository;
        this.telemetryDataRepository = telemetryDataRepository;
        this.equipmentStateRepository = equipmentStateRepository;
        this.processEquipmentAnomaliesUseCase = processEquipmentAnomaliesUseCase;
    }

    public void execute(TelemetryPayloadDTO payloadDTO, String rawPayload) {
        Equipment equipment = equipmentRepository.findByEquipCode(payloadDTO.equipCode())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", payloadDTO.equipCode()));

        Gateway gateway = gatewayRepository.findByCode(payloadDTO.gatewayCode())
                .orElseThrow(() -> new ResourceNotFoundException("Gateway", payloadDTO.gatewayCode()));

        if (equipment.getGateway() == null || !equipment.getGateway().getGatewayCode().equals(gateway.getGatewayCode())) {
            throw new GatewayMismatchException("Gateway does not belong to equipment");
        }

        OffsetDateTime timestamp = payloadDTO.timestamp() != null ? payloadDTO.timestamp() : OffsetDateTime.now();

        Map<UUID, BigDecimal> sensorReadings = new HashMap<>();

        for (ReadingDTO reading : payloadDTO.readings()) {
            Sensor sensor = sensorRepository.findByCode(reading.sensorCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Sensor", reading.sensorCode()));

            telemetryDataRepository.save(new TelemetryData(sensor, timestamp, reading.value()));
            sensorReadings.put(sensor.getSensorId(), reading.value());
        }

        HealthStatus existingStatus = equipmentStateRepository.findByEquipmentId(equipment.getEquipmentId())
                .map(EquipmentState::getStatus)
                .orElse(HealthStatus.HEALTH);

        HealthStatus status = processEquipmentAnomaliesUseCase
                .execute(equipment, sensorReadings)
                .orElse(existingStatus);

        equipmentStateRepository.save(new EquipmentState(equipment, status, rawPayload, timestamp));
    }
}
