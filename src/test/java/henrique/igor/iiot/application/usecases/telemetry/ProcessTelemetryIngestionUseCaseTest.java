package henrique.igor.iiot.application.usecases.telemetry;

import henrique.igor.iiot.application.usecases.telemetry.dto.ProcessTelemetryIngestionInput;
import henrique.igor.iiot.application.usecases.telemetry.dto.TelemetryReadingInput;
import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.GatewayMismatchException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessTelemetryIngestionUseCaseTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private GatewayRepository gatewayRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private TelemetryDataRepository telemetryDataRepository;

    @Mock
    private EquipmentStateRepository equipmentStateRepository;

    @Mock
    private ProcessEquipmentAnomaliesUseCase processEquipmentAnomaliesUseCase;

    @InjectMocks
    private ProcessTelemetryIngestionUseCase processTelemetryIngestionUseCase;

    private Gateway gateway;
    private Equipment equipment;
    private Sensor voltageSensor;
    private Sensor tempSensor;
    private ProcessTelemetryIngestionInput input;
    private String rawPayload;
    private OffsetDateTime timestamp;

    @BeforeEach
    void setUp() {
        timestamp = OffsetDateTime.now();
        rawPayload = "{\"equipCode\":\"MTR-USI-001\",\"gatewayCode\":\"GW-USI-001\"}";

        gateway = new Gateway(UUID.randomUUID(), "GW-USI-001", "MAC-1", "192.168.1.1", "v1", GatewayStatus.ONLINE, null, null, null);
        equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, gateway, OffsetDateTime.now());

        voltageSensor = new Sensor(UUID.randomUUID(), "VOL-001-MTR-USI-001", SensorType.VOLTAGE, SensorStatus.ONLINE, "V", "topic/v", equipment, OffsetDateTime.now());
        tempSensor = new Sensor(UUID.randomUUID(), "TEM-001-MTR-USI-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "C", "topic/t", equipment, OffsetDateTime.now());

        List<TelemetryReadingInput> readings = List.of(
                new TelemetryReadingInput("VOL-001-MTR-USI-001", new BigDecimal("380.5")),
                new TelemetryReadingInput("TEM-001-MTR-USI-001", new BigDecimal("65.2"))
        );

        input = new ProcessTelemetryIngestionInput(
                "MTR-USI-001",
                "GW-USI-001",
                timestamp,
                readings,
                rawPayload
        );
    }

    @Test
    @DisplayName("Should process telemetry ingestion successfully when initial equipment state does not exist")
    void shouldProcessTelemetryIngestionSuccessfullyWhenStateDoesNotExist() {
        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.of(gateway));
        when(sensorRepository.findByCode("VOL-001-MTR-USI-001")).thenReturn(Optional.of(voltageSensor));
        when(sensorRepository.findByCode("TEM-001-MTR-USI-001")).thenReturn(Optional.of(tempSensor));
        when(equipmentStateRepository.findByEquipmentId(equipment.getEquipmentId())).thenReturn(Optional.empty());
        when(processEquipmentAnomaliesUseCase.execute(eq(equipment), any())).thenReturn(Optional.empty());

        processTelemetryIngestionUseCase.execute(input);

        verify(telemetryDataRepository, times(2)).save(any(TelemetryData.class));

        ArgumentCaptor<EquipmentState> stateCaptor = ArgumentCaptor.forClass(EquipmentState.class);
        verify(equipmentStateRepository, times(1)).save(stateCaptor.capture());

        EquipmentState savedState = stateCaptor.getValue();
        assertEquals(equipment, savedState.getEquipmentId());
        assertEquals(HealthStatus.HEALTH, savedState.getStatus());
        assertEquals(rawPayload, savedState.getLastPayload());
        assertEquals(timestamp, savedState.getUpdatedAt());
    }

    @Test
    @DisplayName("Should process telemetry ingestion successfully and preserve existing equipment health status when no rules exist")
    void shouldProcessTelemetryIngestionSuccessfullyWhenStateAlreadyExists() {
        EquipmentState existingState = new EquipmentState(equipment, HealthStatus.WARNING, "old-payload", timestamp.minusMinutes(5));

        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.of(gateway));
        when(sensorRepository.findByCode("VOL-001-MTR-USI-001")).thenReturn(Optional.of(voltageSensor));
        when(sensorRepository.findByCode("TEM-001-MTR-USI-001")).thenReturn(Optional.of(tempSensor));
        when(equipmentStateRepository.findByEquipmentId(equipment.getEquipmentId())).thenReturn(Optional.of(existingState));
        when(processEquipmentAnomaliesUseCase.execute(eq(equipment), any())).thenReturn(Optional.empty());

        processTelemetryIngestionUseCase.execute(input);

        verify(telemetryDataRepository, times(2)).save(any(TelemetryData.class));

        ArgumentCaptor<EquipmentState> stateCaptor = ArgumentCaptor.forClass(EquipmentState.class);
        verify(equipmentStateRepository, times(1)).save(stateCaptor.capture());

        EquipmentState savedState = stateCaptor.getValue();
        assertEquals(HealthStatus.WARNING, savedState.getStatus());
        assertEquals(rawPayload, savedState.getLastPayload());
    }

    @Test
    @DisplayName("Should update health status to CRITICAL when anomaly processor detects critical breach")
    void shouldUpdateHealthStatusToCriticalWhenAnomalyProcessorDetectsBreach() {
        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.of(gateway));
        when(sensorRepository.findByCode("VOL-001-MTR-USI-001")).thenReturn(Optional.of(voltageSensor));
        when(sensorRepository.findByCode("TEM-001-MTR-USI-001")).thenReturn(Optional.of(tempSensor));
        when(equipmentStateRepository.findByEquipmentId(equipment.getEquipmentId())).thenReturn(Optional.empty());
        when(processEquipmentAnomaliesUseCase.execute(eq(equipment), any())).thenReturn(Optional.of(HealthStatus.CRITICAL));

        processTelemetryIngestionUseCase.execute(input);

        ArgumentCaptor<EquipmentState> stateCaptor = ArgumentCaptor.forClass(EquipmentState.class);
        verify(equipmentStateRepository, times(1)).save(stateCaptor.capture());
        assertEquals(HealthStatus.CRITICAL, stateCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when equipment is not found")
    void shouldThrowExceptionWhenEquipmentNotFound() {
        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> processTelemetryIngestionUseCase.execute(input)
        );

        assertEquals("Equipment not found with identifier: MTR-USI-001", exception.getMessage());
        verifyNoInteractions(sensorRepository, telemetryDataRepository, equipmentStateRepository);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when gateway is not found")
    void shouldThrowExceptionWhenGatewayNotFound() {
        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> processTelemetryIngestionUseCase.execute(input)
        );

        assertEquals("Gateway not found with identifier: GW-USI-001", exception.getMessage());
        verifyNoInteractions(sensorRepository, telemetryDataRepository, equipmentStateRepository);
    }

    @Test
    @DisplayName("Should throw GatewayMismatchException when gateway does not belong to equipment")
    void shouldThrowExceptionWhenGatewayDoesNotBelongToEquipment() {
        Gateway otherGateway = new Gateway(UUID.randomUUID(), "GW-99-OTHER", "MAC-2", "192.168.1.2", "v1", GatewayStatus.ONLINE, null, null, null);

        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.of(otherGateway));

        GatewayMismatchException exception = assertThrows(
                GatewayMismatchException.class,
                () -> processTelemetryIngestionUseCase.execute(input)
        );

        assertEquals("Gateway does not belong to equipment", exception.getMessage());
        verifyNoInteractions(sensorRepository, telemetryDataRepository, equipmentStateRepository);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when sensor is not found")
    void shouldThrowExceptionWhenSensorNotFound() {
        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.of(gateway));
        when(sensorRepository.findByCode("VOL-001-MTR-USI-001")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> processTelemetryIngestionUseCase.execute(input)
        );

        assertEquals("Sensor not found with identifier: VOL-001-MTR-USI-001", exception.getMessage());
        verify(telemetryDataRepository, never()).save(any());
        verify(equipmentStateRepository, never()).save(any());
    }
}
