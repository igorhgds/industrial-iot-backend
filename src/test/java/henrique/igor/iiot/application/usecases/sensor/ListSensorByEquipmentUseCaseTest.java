package henrique.igor.iiot.application.usecases.sensor;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.entities.enums.SensorType;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListSensorByEquipmentUseCaseTest {

    private SensorRepository sensorRepository;
    private EquipmentRepository equipmentRepository;
    private ListSensorByEquipmentUseCase listSensorByEquipmentUseCase;

    @BeforeEach
    void setUp() {
        sensorRepository = mock(SensorRepository.class);
        equipmentRepository = mock(EquipmentRepository.class);
        listSensorByEquipmentUseCase = new ListSensorByEquipmentUseCase(sensorRepository, equipmentRepository);
    }

    @Test
    @DisplayName("Should list sensors by equipment ID successfully")
    void shouldListSensorsByEquipmentSuccessfully() {
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());

        Sensor sensor1 = new Sensor(UUID.randomUUID(), "TEM-001-MTR-USI-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic/1", equipment, OffsetDateTime.now());
        Sensor sensor2 = new Sensor(UUID.randomUUID(), "VIB-001-MTR-USI-001", SensorType.VIBRATION, SensorStatus.ONLINE, "mm/s", "topic/2", equipment, OffsetDateTime.now());

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(sensorRepository.findByEquipmentId(equipmentId)).thenReturn(List.of(sensor1, sensor2));

        List<Sensor> result = listSensorByEquipmentUseCase.execute(equipmentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("TEM-001-MTR-USI-001", result.get(0).getCode());
        assertEquals("VIB-001-MTR-USI-001", result.get(1).getCode());

        verify(equipmentRepository, times(1)).findById(equipmentId);
        verify(sensorRepository, times(1)).findByEquipmentId(equipmentId);
    }

    @Test
    @DisplayName("Should return empty list when equipment has no sensors")
    void shouldReturnEmptyListWhenEquipmentHasNoSensors() {
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(sensorRepository.findByEquipmentId(equipmentId)).thenReturn(Collections.emptyList());

        List<Sensor> result = listSensorByEquipmentUseCase.execute(equipmentId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(equipmentRepository, times(1)).findById(equipmentId);
        verify(sensorRepository, times(1)).findByEquipmentId(equipmentId);
    }

    @Test
    @DisplayName("Should throw exception when equipmentId is null")
    void shouldThrowExceptionWhenEquipmentIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> listSensorByEquipmentUseCase.execute(null));

        assertEquals("Equipment ID cannot be null.", exception.getMessage());
        verify(equipmentRepository, never()).findById(any());
        verify(sensorRepository, never()).findByEquipmentId(any());
    }

    @Test
    @DisplayName("Should throw exception when equipment is not found")
    void shouldThrowExceptionWhenEquipmentNotFound() {
        UUID equipmentId = UUID.randomUUID();
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> listSensorByEquipmentUseCase.execute(equipmentId));

        assertEquals("Equipment not found with ID: " + equipmentId, exception.getMessage());
        verify(equipmentRepository, times(1)).findById(equipmentId);
        verify(sensorRepository, never()).findByEquipmentId(any());
    }
}