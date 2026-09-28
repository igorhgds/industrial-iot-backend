package henrique.igor.iiot.application.usecases.equipment;

import henrique.igor.iiot.application.usecases.equipment.dto.UpdateEquipmentRequest;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.GatewayStatus;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateEquipmentUseCaseTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private GatewayRepository gatewayRepository;

    @InjectMocks
    private UpdateEquipmentUseCase updateEquipmentUseCase;

    @Test
    @DisplayName("Should update equipment successfully using Rich Domain Model methods")
    void shouldUpdateEquipmentSuccessfully() {
        // 1. ARRANGE
        UUID equipmentId = UUID.randomUUID();
        UUID newSectorId = UUID.randomUUID();
        UUID newGatewayId = UUID.randomUUID();

        Sector newSector = new Sector(newSectorId, "Assembly Line", "Desc", null);
        Gateway newGateway = new Gateway(newGatewayId, "GW-02", "MAC-2", "192.168.1.2", "v1", GatewayStatus.ONLINE, newSector, null, null);
        Equipment existingEquipment = new Equipment(equipmentId, "MTR-01", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, null);

        UpdateEquipmentRequest request = new UpdateEquipmentRequest(
                equipmentId,
                EquipStatus.MAINTENANCE,
                newSectorId,
                newGatewayId
        );

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(existingEquipment));
        when(sectorRepository.findById(newSectorId)).thenReturn(Optional.of(newSector));
        when(gatewayRepository.findById(newGatewayId)).thenReturn(Optional.of(newGateway));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. ACT
        Equipment updatedEquipment = updateEquipmentUseCase.execute(request);

        // 3. ASSERT
        assertNotNull(updatedEquipment);
        assertEquals(equipmentId, updatedEquipment.getEquipmentId());
        assertEquals(EquipType.MOTOR, updatedEquipment.getType()); // Type permanece o imutável original
        assertEquals(EquipStatus.MAINTENANCE, updatedEquipment.getStatus());
        assertEquals(newSector, updatedEquipment.getSector());
        assertEquals(newGateway, updatedEquipment.getGateway());

        verify(equipmentRepository, times(1)).findById(equipmentId);
        verify(sectorRepository, times(1)).findById(newSectorId);
        verify(gatewayRepository, times(1)).findById(newGatewayId);
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when equipment is not found")
    void shouldThrowExceptionWhenEquipmentNotFound() {
        // 1. ARRANGE
        UUID nonExistentId = UUID.randomUUID();
        UpdateEquipmentRequest request = new UpdateEquipmentRequest(nonExistentId, EquipStatus.ACTIVE, null, null);

        when(equipmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // 2. ACT & 3. ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateEquipmentUseCase.execute(request)
        );

        assertEquals("Equipment not found with ID: " + nonExistentId, exception.getMessage());
        verify(equipmentRepository, times(1)).findById(nonExistentId);
        verify(equipmentRepository, never()).save(any());
    }
}
