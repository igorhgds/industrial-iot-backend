package henrique.igor.iiot.application.usecases.actuator;

import henrique.igor.iiot.application.usecases.actuator.dto.UpdateActuatorInput;
import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.enums.ActuatorStatus;
import henrique.igor.iiot.domain.entities.enums.ActuatorType;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.repositories.ActuatorRepository;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.services.ActuatorCodeGenerator;
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
class UpdateActuatorUseCaseTest {

    @Mock
    private ActuatorRepository actuatorRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private ActuatorCodeGenerator actuatorCodeGenerator;

    @InjectMocks
    private UpdateActuatorUseCase updateActuatorUseCase;

    @Test
    @DisplayName("Should update actuator status and MQTT topics successfully")
    void shouldUpdateActuatorStatusAndMqttTopics() {
        UUID actuatorId = UUID.randomUUID();
        Actuator existing = new Actuator(actuatorId, "RLY-001-MTR-USI-001", ActuatorType.RELAY, ActuatorStatus.OFFLINE, "old/cmd", "old/state", null, OffsetDateTime.now());

        UpdateActuatorInput input = new UpdateActuatorInput(
                actuatorId,
                ActuatorStatus.ONLINE,
                "new/cmd",
                "new/state",
                null
        );

        when(actuatorRepository.findById(actuatorId)).thenReturn(Optional.of(existing));
        when(actuatorRepository.save(any(Actuator.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Actuator result = updateActuatorUseCase.execute(input);

        assertNotNull(result);
        assertEquals(ActuatorStatus.ONLINE, result.getStatus());
        assertEquals("new/cmd", result.getCommandMqttTopic());
        assertEquals("new/state", result.getStateMqttTopic());

        verify(actuatorRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Should relocate actuator to new equipment and generate new code")
    void shouldRelocateActuatorToNewEquipment() {
        UUID actuatorId = UUID.randomUUID();
        UUID newEquipId = UUID.randomUUID();

        Equipment newEquipment = new Equipment(newEquipId, "CMP-USI-002", EquipType.COMPRESSOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Actuator existing = new Actuator(actuatorId, "RLY-001-GEN", ActuatorType.RELAY, ActuatorStatus.OFFLINE, "cmd", "state", null, OffsetDateTime.now());

        UpdateActuatorInput input = new UpdateActuatorInput(
                actuatorId,
                null,
                null,
                null,
                newEquipId
        );

        when(actuatorRepository.findById(actuatorId)).thenReturn(Optional.of(existing));
        when(equipmentRepository.findById(newEquipId)).thenReturn(Optional.of(newEquipment));
        when(actuatorCodeGenerator.generate(ActuatorType.RELAY, newEquipment)).thenReturn("RLY-001-CMP-USI-002");
        when(actuatorRepository.save(any(Actuator.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Actuator result = updateActuatorUseCase.execute(input);

        assertNotNull(result);
        assertEquals(newEquipment, result.getEquipment());
        assertEquals("RLY-001-CMP-USI-002", result.getCode());

        verify(actuatorCodeGenerator, times(1)).generate(ActuatorType.RELAY, newEquipment);
        verify(actuatorRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when actuator is not found")
    void shouldThrowExceptionWhenActuatorNotFound() {
        UUID actuatorId = UUID.randomUUID();
        UpdateActuatorInput input = new UpdateActuatorInput(actuatorId, ActuatorStatus.ONLINE, null, null, null);

        when(actuatorRepository.findById(actuatorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> updateActuatorUseCase.execute(input));

        assertEquals("Actuator not found with ID: " + actuatorId, exception.getMessage());
        verify(actuatorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when new equipment is not found")
    void shouldThrowExceptionWhenNewEquipmentNotFound() {
        UUID actuatorId = UUID.randomUUID();
        UUID newEquipId = UUID.randomUUID();

        Actuator existing = new Actuator(actuatorId, "RLY-001-GEN", ActuatorType.RELAY, ActuatorStatus.OFFLINE, "cmd", "state", null, OffsetDateTime.now());
        UpdateActuatorInput input = new UpdateActuatorInput(actuatorId, null, null, null, newEquipId);

        when(actuatorRepository.findById(actuatorId)).thenReturn(Optional.of(existing));
        when(equipmentRepository.findById(newEquipId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> updateActuatorUseCase.execute(input));

        assertEquals("Equipment not found with ID: " + newEquipId, exception.getMessage());
        verify(actuatorRepository, never()).save(any());
    }
}
