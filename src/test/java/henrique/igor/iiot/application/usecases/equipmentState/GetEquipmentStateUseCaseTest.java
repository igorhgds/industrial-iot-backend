package henrique.igor.iiot.application.usecases.equipmentState;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.EquipmentState;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.HealthStatus;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.EquipmentStateRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEquipmentStateUseCaseTest {

    @Mock
    private EquipmentStateRepository equipmentStateRepository;

    @InjectMocks
    private GetEquipmentStateUseCase getEquipmentStateUseCase;

    @Test
    @DisplayName("Should return equipment state when state exists for equipmentId")
    void shouldReturnEquipmentStateWhenStateExists() {
        // ARRANGE
        UUID equipmentId = UUID.randomUUID();
        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, null);
        EquipmentState expectedState = new EquipmentState(
                equipment,
                HealthStatus.OPERATIONAL,
                "{\"equipCode\":\"MTR-USI-001\"}",
                OffsetDateTime.now()
        );

        when(equipmentStateRepository.findByEquipmentId(equipmentId)).thenReturn(Optional.of(expectedState));

        // ACT
        EquipmentState actualState = getEquipmentStateUseCase.execute(equipmentId);

        // ASSERT
        assertNotNull(actualState);
        assertEquals(equipmentId, actualState.getEquipmentId().getEquipmentId());
        assertEquals(HealthStatus.OPERATIONAL, actualState.getStatus());
        assertEquals("{\"equipCode\":\"MTR-USI-001\"}", actualState.getLastPayload());

        verify(equipmentStateRepository, times(1)).findByEquipmentId(equipmentId);
    }

    @Test
    @DisplayName("Should throw DomainValidationException when equipmentId is null")
    void shouldThrowExceptionWhenEquipmentIdIsNull() {
        // ACT & ASSERT
        DomainValidationException exception = assertThrows(
                DomainValidationException.class,
                () -> getEquipmentStateUseCase.execute(null)
        );

        assertEquals("Equipment ID cannot be null.", exception.getMessage());
        verify(equipmentStateRepository, never()).findByEquipmentId(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when equipment state is not found")
    void shouldThrowExceptionWhenEquipmentStateNotFound() {
        // ARRANGE
        UUID nonExistentId = UUID.randomUUID();
        when(equipmentStateRepository.findByEquipmentId(nonExistentId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getEquipmentStateUseCase.execute(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("EquipmentState not found with identifier: " + nonExistentId));
        verify(equipmentStateRepository, times(1)).findByEquipmentId(nonExistentId);
    }
}
