package henrique.igor.iiot.application.usecases.sensor;

import henrique.igor.iiot.application.usecases.sensor.dto.UpdateSensorInput;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.entities.enums.SensorType;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;
import henrique.igor.iiot.domain.services.SensorCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSensorUseCaseTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private SensorCodeGenerator sensorCodeGenerator;

    @InjectMocks
    private UpdateSensorUseCase updateSensorUseCase;

    @Test
    @DisplayName("Should update sensor status and MQTT topic successfully")
    void shouldUpdateSensorStatusAndMqttTopic() {
        UUID sensorId = UUID.randomUUID();
        Sensor existingSensor = new Sensor(sensorId, "TEM-001-MTR-USI-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "old/topic", null, OffsetDateTime.now());

        UpdateSensorInput request = new UpdateSensorInput(
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

        UpdateSensorInput request = new UpdateSensorInput(
                sensorId,
                null,
                null,
                newEquipId
        );

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(existingSensor));
        when(equipmentRepository.findById(newEquipId)).thenReturn(Optional.of(newEquipment));
        when(sensorCodeGenerator.generate(SensorType.TEMPERATURE, newEquipment)).thenReturn("TEM-001-CMP-USI-002");
        when(sensorRepository.save(any(Sensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sensor updatedSensor = updateSensorUseCase.execute(request);

        assertNotNull(updatedSensor);
        assertEquals(newEquipment, updatedSensor.getEquipment());
        assertEquals("TEM-001-CMP-USI-002", updatedSensor.getCode());

        verify(sensorCodeGenerator, times(1)).generate(SensorType.TEMPERATURE, newEquipment);
        verify(sensorRepository, times(1)).save(existingSensor);
    }

    @Test
    @DisplayName("Should throw exception when sensor not found")
    void shouldThrowExceptionWhenSensorNotFound() {
        UUID sensorId = UUID.randomUUID();
        UpdateSensorInput request = new UpdateSensorInput(sensorId, SensorStatus.ONLINE, null, null);

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
        UpdateSensorInput request = new UpdateSensorInput(sensorId, null, null, newEquipId);

        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(existingSensor));
        when(equipmentRepository.findById(newEquipId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> updateSensorUseCase.execute(request));

        assertEquals("Equipment not found with ID: " + newEquipId, exception.getMessage());
    }
}
