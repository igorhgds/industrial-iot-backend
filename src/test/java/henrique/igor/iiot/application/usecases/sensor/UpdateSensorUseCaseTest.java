package henrique.igor.iiot.application.usecases.sensor;

import henrique.igor.iiot.application.usecases.sensor.dto.UpdateSensorRequest;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.entities.enums.SensorType;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;
import henrique.igor.iiot.domain.services.SensorCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateSensorUseCaseTest {

    private SensorRepository sensorRepository;
    private EquipmentRepository equipmentRepository;
    private SensorCodeGenerator sensorCodeGenerator;
    private UpdateSensorUseCase updateSensorUseCase;

    @BeforeEach
    void setUp() {
        sensorRepository = mock(SensorRepository.class);
        equipmentRepository = mock(EquipmentRepository.class);
        sensorCodeGenerator = new SensorCodeGenerator(sensorRepository);
        updateSensorUseCase = new UpdateSensorUseCase(sensorRepository, equipmentRepository, sensorCodeGenerator);
    }

    @Test
    @DisplayName("Should update sensor status and MQTT topic successfully")
    void shouldUpdateSensorStatusAndMqttTopic() {
        UUID sensorId = UUID.randomUUID();
        Sensor existingSensor = new Sensor(sensorId, "TEM-001-MTR-USI-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "old/topic", null, OffsetDateTime.now());

        UpdateSensorRequest request = new UpdateSensorRequest(
                sensorId,
                SensorStatus.OFFLINE,
                "new/topic",
                null
        );

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(existingSensor));
        when(sensorRepository.save(any(Sensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sensor updatedSensor = updateSensorUseCase.execute(request);

        assertNotNull(updatedSensor);
        assertEquals(SensorStatus.OFFLINE, updatedSensor.getStatus());
        assertEquals("new/topic", updatedSensor.getMqttTopic());
        assertEquals("TEM-001-MTR-USI-001", updatedSensor.getCode());

        verify(sensorRepository, times(1)).save(existingSensor);
    }

    @Test
    @DisplayName("Should relocate sensor to new equipment and regenerate sensor code")
    void shouldRelocateSensorToNewEquipmentAndGenerateNewCode() {
        UUID sensorId = UUID.randomUUID();
        UUID oldEquipId = UUID.randomUUID();
        UUID newEquipId = UUID.randomUUID();

        Equipment oldEquipment = new Equipment(oldEquipId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Equipment newEquipment = new Equipment(newEquipId, "CMP-USI-002", EquipType.COMPRESSOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());

        Sensor existingSensor = new Sensor(sensorId, "TEM-001-MTR-USI-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "telemetry/tem", oldEquipment, OffsetDateTime.now());

        UpdateSensorRequest request = new UpdateSensorRequest(
                sensorId,
                null,
                null,
                newEquipId
        );

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(existingSensor));
        when(equipmentRepository.findById(newEquipId)).thenReturn(Optional.of(newEquipment));
        when(sensorRepository.countByEquipmentIdAndSensorType(newEquipId, SensorType.TEMPERATURE)).thenReturn(0L);
        when(sensorRepository.save(any(Sensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sensor updatedSensor = updateSensorUseCase.execute(request);

        assertNotNull(updatedSensor);
        assertEquals(newEquipment, updatedSensor.getEquipment());
        assertEquals("TEM-001-CMP-USI-002", updatedSensor.getCode());

        verify(sensorRepository, times(1)).save(existingSensor);
    }

    @Test
    @DisplayName("Should throw exception when sensor not found")
    void shouldThrowExceptionWhenSensorNotFound() {
        UUID sensorId = UUID.randomUUID();
        UpdateSensorRequest request = new UpdateSensorRequest(sensorId, SensorStatus.ONLINE, null, null);

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> updateSensorUseCase.execute(request));

        assertEquals("Sensor not found with ID: " + sensorId, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when new equipment not found")
    void shouldThrowExceptionWhenNewEquipmentNotFound() {
        UUID sensorId = UUID.randomUUID();
        UUID newEquipId = UUID.randomUUID();

        Sensor existingSensor = new Sensor(sensorId, "TEM-001-GEN", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", null, OffsetDateTime.now());
        UpdateSensorRequest request = new UpdateSensorRequest(sensorId, null, null, newEquipId);

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(existingSensor));
        when(equipmentRepository.findById(newEquipId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> updateSensorUseCase.execute(request));

        assertEquals("Equipment not found with ID: " + newEquipId, exception.getMessage());
    }
}
