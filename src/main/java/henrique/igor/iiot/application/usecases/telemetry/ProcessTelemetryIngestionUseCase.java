package henrique.igor.iiot.application.usecases.telemetry;

import henrique.igor.iiot.application.usecases.telemetry.dto.ProcessTelemetryIngestionInput;
import henrique.igor.iiot.application.usecases.telemetry.dto.TelemetryReadingInput;
import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.domain.entities.enums.HealthStatus;
import henrique.igor.iiot.domain.exceptions.GatewayMismatchException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.*;

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

    public void execute(ProcessTelemetryIngestionInput input) {
        Equipment equipment = equipmentRepository.findByEquipCode(input.equipCode())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", input.equipCode()));

        Gateway gateway = gatewayRepository.findByCode(input.gatewayCode())
                .orElseThrow(() -> new ResourceNotFoundException("Gateway", input.gatewayCode()));

        if (equipment.getGateway() == null || !equipment.getGateway().getGatewayCode().equals(gateway.getGatewayCode())) {
            throw new GatewayMismatchException("Gateway does not belong to equipment");
        }

        OffsetDateTime timestamp = input.timestamp() != null ? input.timestamp() : OffsetDateTime.now();

        Map<UUID, BigDecimal> sensorReadings = new HashMap<>();

        for (TelemetryReadingInput reading : input.readings()) {
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

        equipmentStateRepository.save(new EquipmentState(equipment, status, input.rawPayload(), timestamp));
    }
}
