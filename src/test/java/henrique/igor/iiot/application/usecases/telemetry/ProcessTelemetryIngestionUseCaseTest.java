package henrique.igor.iiot.application.usecases.telemetry;

import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.GatewayStatus;
import henrique.igor.iiot.domain.entities.enums.HealthStatus;
import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.entities.enums.SensorType;
import henrique.igor.iiot.domain.repositories.*;
import henrique.igor.iiot.infrastructure.messaging.dto.ReadingDTO;
import henrique.igor.iiot.infrastructure.messaging.dto.TelemetryPayloadDTO;
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

    @InjectMocks
    private ProcessTelemetryIngestionUseCase processTelemetryIngestionUseCase;

    private Gateway gateway;
    private Equipment equipment;
    private Sensor voltageSensor;
    private Sensor tempSensor;
    private TelemetryPayloadDTO payloadDTO;
    private String rawPayload;
    private OffsetDateTime timestamp;

    @BeforeEach
    void setUp() {
        timestamp = OffsetDateTime.now();
        rawPayload = "{\"equipCode\":\"MTR-USI-001\",\"gatewayCode\":\"GW-USI-001\"}";

        // Codes aligned with EquipmentCodeGenerator, GatewayCodeGenerator, SensorCodeGenerator
        gateway = new Gateway(UUID.randomUUID(), "GW-USI-001", "MAC-1", "192.168.1.1", "v1", GatewayStatus.ONLINE, null, null, null);
        equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, gateway, OffsetDateTime.now());

        voltageSensor = new Sensor(UUID.randomUUID(), "VOL-001-MTR-USI-001", SensorType.VOLTAGE, SensorStatus.ONLINE, "V", "topic/v", equipment, OffsetDateTime.now());
        tempSensor = new Sensor(UUID.randomUUID(), "TEM-001-MTR-USI-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "C", "topic/t", equipment, OffsetDateTime.now());

        List<ReadingDTO> readings = List.of(
                new ReadingDTO("VOL-001-MTR-USI-001", new BigDecimal("380.5")),
                new ReadingDTO("TEM-001-MTR-USI-001", new BigDecimal("65.2"))
        );

        payloadDTO = new TelemetryPayloadDTO(
                "MTR-USI-001",
                "GW-USI-001",
                timestamp,
                readings
        );
    }

    @Test
    @DisplayName("Should process telemetry ingestion successfully when initial equipment state does not exist")
    void shouldProcessTelemetryIngestionSuccessfullyWhenStateDoesNotExist() {
        // ARRANGE
        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.of(gateway));
        when(sensorRepository.findByCode("VOL-001-MTR-USI-001")).thenReturn(Optional.of(voltageSensor));
        when(sensorRepository.findByCode("TEM-001-MTR-USI-001")).thenReturn(Optional.of(tempSensor));
        when(equipmentStateRepository.findByEquipmentId(equipment.getEquipmentId())).thenReturn(Optional.empty());

        // ACT
        processTelemetryIngestionUseCase.execute(payloadDTO, rawPayload);

        // ASSERT & VERIFY
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
    @DisplayName("Should process telemetry ingestion successfully and preserve existing equipment health status")
    void shouldProcessTelemetryIngestionSuccessfullyWhenStateAlreadyExists() {
        // ARRANGE
        EquipmentState existingState = new EquipmentState(equipment, HealthStatus.WARNING, "old-payload", timestamp.minusMinutes(5));

        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.of(gateway));
        when(sensorRepository.findByCode("VOL-001-MTR-USI-001")).thenReturn(Optional.of(voltageSensor));
        when(sensorRepository.findByCode("TEM-001-MTR-USI-001")).thenReturn(Optional.of(tempSensor));
        when(equipmentStateRepository.findByEquipmentId(equipment.getEquipmentId())).thenReturn(Optional.of(existingState));

        // ACT
        processTelemetryIngestionUseCase.execute(payloadDTO, rawPayload);

        // ASSERT & VERIFY
        verify(telemetryDataRepository, times(2)).save(any(TelemetryData.class));

        ArgumentCaptor<EquipmentState> stateCaptor = ArgumentCaptor.forClass(EquipmentState.class);
        verify(equipmentStateRepository, times(1)).save(stateCaptor.capture());

        EquipmentState savedState = stateCaptor.getValue();
        assertEquals(HealthStatus.WARNING, savedState.getStatus());
        assertEquals(rawPayload, savedState.getLastPayload());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when equipment is not found")
    void shouldThrowExceptionWhenEquipmentNotFound() {
        // ARRANGE
        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.empty());

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processTelemetryIngestionUseCase.execute(payloadDTO, rawPayload)
        );

        assertEquals("Equipment not found", exception.getMessage());
        verifyNoInteractions(sensorRepository, telemetryDataRepository, equipmentStateRepository);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when gateway is not found")
    void shouldThrowExceptionWhenGatewayNotFound() {
        // ARRANGE
        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.empty());

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processTelemetryIngestionUseCase.execute(payloadDTO, rawPayload)
        );

        assertEquals("Gateway not found", exception.getMessage());
        verifyNoInteractions(sensorRepository, telemetryDataRepository, equipmentStateRepository);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when gateway does not belong to equipment")
    void shouldThrowExceptionWhenGatewayDoesNotBelongToEquipment() {
        // ARRANGE
        Gateway otherGateway = new Gateway(UUID.randomUUID(), "GW-99-OTHER", "MAC-2", "192.168.1.2", "v1", GatewayStatus.ONLINE, null, null, null);

        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.of(otherGateway));

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processTelemetryIngestionUseCase.execute(payloadDTO, rawPayload)
        );

        assertEquals("Gateway does not belong to equipment", exception.getMessage());
        verifyNoInteractions(sensorRepository, telemetryDataRepository, equipmentStateRepository);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when sensor is not found")
    void shouldThrowExceptionWhenSensorNotFound() {
        // ARRANGE
        when(equipmentRepository.findByEquipCode("MTR-USI-001")).thenReturn(Optional.of(equipment));
        when(gatewayRepository.findByCode("GW-USI-001")).thenReturn(Optional.of(gateway));
        when(sensorRepository.findByCode("VOL-001-MTR-USI-001")).thenReturn(Optional.empty());

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> processTelemetryIngestionUseCase.execute(payloadDTO, rawPayload)
        );

        assertEquals("Sensor not found", exception.getMessage());
        verify(telemetryDataRepository, never()).save(any());
        verify(equipmentStateRepository, never()).save(any());
    }
}