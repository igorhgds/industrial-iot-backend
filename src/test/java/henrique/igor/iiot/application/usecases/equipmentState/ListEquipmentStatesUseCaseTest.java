package henrique.igor.iiot.application.usecases.equipmentState;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.EquipmentState;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.HealthStatus;
import henrique.igor.iiot.domain.repositories.EquipmentStateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListEquipmentStatesUseCaseTest {

    @Mock
    private EquipmentStateRepository equipmentStateRepository;

    @InjectMocks
    private ListEquipmentStatesUseCase listEquipmentStatesUseCase;

    @Test
    @DisplayName("Should return list of equipment states when states exist")
    void shouldReturnListOfEquipmentStatesWhenStatesExist() {
        // ARRANGE
        Equipment equip1 = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, null);
        Equipment equip2 = new Equipment(UUID.randomUUID(), "MTR-USI-002", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, null);

        EquipmentState state1 = new EquipmentState(equip1, HealthStatus.OPERATIONAL, "{}", OffsetDateTime.now());
        EquipmentState state2 = new EquipmentState(equip2, HealthStatus.WARNING, "{}", OffsetDateTime.now());

        when(equipmentStateRepository.findAll()).thenReturn(List.of(state1, state2));

        // ACT
        List<EquipmentState> actualStates = listEquipmentStatesUseCase.execute();

        // ASSERT
        assertNotNull(actualStates);
        assertEquals(2, actualStates.size());
        assertEquals(HealthStatus.OPERATIONAL, actualStates.get(0).getStatus());
        assertEquals(HealthStatus.WARNING, actualStates.get(1).getStatus());

        verify(equipmentStateRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no equipment states exist")
    void shouldReturnEmptyListWhenNoStatesExist() {
        // ARRANGE
        when(equipmentStateRepository.findAll()).thenReturn(Collections.emptyList());

        // ACT
        List<EquipmentState> actualStates = listEquipmentStatesUseCase.execute();

        // ASSERT
        assertNotNull(actualStates);
        assertTrue(actualStates.isEmpty());

        verify(equipmentStateRepository, times(1)).findAll();
    }
}
