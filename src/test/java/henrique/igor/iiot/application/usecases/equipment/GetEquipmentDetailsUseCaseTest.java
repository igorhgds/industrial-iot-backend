package henrique.igor.iiot.application.usecases.equipment;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEquipmentDetailsUseCaseTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private GetEquipmentDetailsUseCase getEquipmentDetailsUseCase;

    @Test
    @DisplayName("Should return equipment details when equipment exists")
    void shouldReturnEquipmentDetailsWhenEquipmentExists() {
        // ARRANGE
        UUID equipmentId = UUID.randomUUID();
        Equipment expectedEquipment = new Equipment(equipmentId, "MTR-01", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, null);

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(expectedEquipment));

        // ACT
        Equipment actualEquipment = getEquipmentDetailsUseCase.execute(equipmentId);

        // ASSERT
        assertNotNull(actualEquipment);
        assertEquals(equipmentId, actualEquipment.getEquipmentId());
        assertEquals("MTR-01", actualEquipment.getEquipCode());
        assertEquals(EquipType.MOTOR, actualEquipment.getType());

        verify(equipmentRepository, times(1)).findById(equipmentId);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when equipmentId is null")
    void shouldThrowExceptionWhenEquipmentIdIsNull() {
        // ARRANGE
        UUID nullId = null;

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getEquipmentDetailsUseCase.execute(nullId)
        );

        assertEquals("Equipment ID cannot be null.", exception.getMessage());
        verify(equipmentRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when equipment is not found")
    void shouldThrowExceptionWhenEquipmentNotFound() {
        // ARRANGE
        UUID nonExistentId = UUID.randomUUID();

        when(equipmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getEquipmentDetailsUseCase.execute(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("Equipment not found with ID: " + nonExistentId));
        verify(equipmentRepository, times(1)).findById(nonExistentId);
    }
}
