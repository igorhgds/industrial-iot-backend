package henrique.igor.iiot.application.usecases.equipment;

import henrique.igor.iiot.application.usecases.equipment.dto.RegisterEquipmentInput;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.entities.enums.GatewayStatus;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import henrique.igor.iiot.domain.services.EquipmentCodeGenerator;
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
class RegisterEquipmentUseCaseTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private GatewayRepository gatewayRepository;

    @Mock
    private EquipmentCodeGenerator equipmentCodeGenerator;

    @InjectMocks
    private RegisterEquipmentUseCase registerEquipmentUseCase;

    @Test
    @DisplayName("Should register equipment with manual equipCode successfully")
    void shouldRegisterEquipmentWithManualCodeSuccessfully() {
        // ARRANGE
        UUID sectorId = UUID.randomUUID();
        UUID gatewayId = UUID.randomUUID();
        Sector sector = new Sector(sectorId, "Stamping", "Desc", null);
        Gateway gateway = new Gateway(gatewayId, "GW-01", "MAC-1", "192.168.1.1", "v1", GatewayStatus.ONLINE, sector, null, null);

        RegisterEquipmentInput request = new RegisterEquipmentInput(
                "MTR-CUSTOM-01",
                EquipType.MOTOR,
                EquipStatus.ACTIVE,
                sectorId,
                gatewayId
        );

        when(sectorRepository.findById(sectorId)).thenReturn(Optional.of(sector));
        when(gatewayRepository.findById(gatewayId)).thenReturn(Optional.of(gateway));
        when(equipmentRepository.findByEquipCode("MTR-CUSTOM-01")).thenReturn(Optional.empty());
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Equipment createdEquipment = registerEquipmentUseCase.execute(request);

        // ASSERT
        assertNotNull(createdEquipment);
        assertEquals("MTR-CUSTOM-01", createdEquipment.getEquipCode());
        assertEquals(EquipType.MOTOR, createdEquipment.getType());
        assertEquals(EquipStatus.ACTIVE, createdEquipment.getStatus());
        assertEquals(sector, createdEquipment.getSector());
        assertEquals(gateway, createdEquipment.getGateway());

        verify(equipmentRepository, times(1)).findByEquipCode("MTR-CUSTOM-01");
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    @DisplayName("Should auto-generate equipment code (Type-Sector-Seq) when equipCode is null")
    void shouldAutoGenerateEquipmentCodeWhenCodeIsNull() {
        // ARRANGE
        UUID sectorId = UUID.randomUUID();
        Sector sector = new Sector(sectorId, "Usinagem", "Desc", null);

        RegisterEquipmentInput request = new RegisterEquipmentInput(
                null, // Trigger auto-generation
                EquipType.MOTOR,
                EquipStatus.ACTIVE,
                sectorId,
                null
        );

        when(sectorRepository.findById(sectorId)).thenReturn(Optional.of(sector));
        when(equipmentCodeGenerator.generate(EquipType.MOTOR, sector)).thenReturn("MTR-USI-002");
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Equipment createdEquipment = registerEquipmentUseCase.execute(request);

        // ASSERT
        assertNotNull(createdEquipment);
        assertEquals("MTR-USI-002", createdEquipment.getEquipCode());
        assertEquals(EquipType.MOTOR, createdEquipment.getType());

        verify(equipmentCodeGenerator, times(1)).generate(EquipType.MOTOR, sector);
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when equipment type is null")
    void shouldThrowExceptionWhenTypeIsNull() {
        // ARRANGE
        RegisterEquipmentInput request = new RegisterEquipmentInput("MTR-01", null, EquipStatus.ACTIVE, null, null);

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registerEquipmentUseCase.execute(request)
        );

        assertEquals("Equipment type cannot be null.", exception.getMessage());
        verify(equipmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when equipCode already exists")
    void shouldThrowExceptionWhenCodeAlreadyExists() {
        // ARRANGE
        RegisterEquipmentInput request = new RegisterEquipmentInput("MTR-DUPLICATE", EquipType.MOTOR, EquipStatus.ACTIVE, null, null);
        Equipment existingEquipment = new Equipment("MTR-DUPLICATE", EquipType.MOTOR, null, null);

        when(equipmentRepository.findByEquipCode("MTR-DUPLICATE")).thenReturn(Optional.of(existingEquipment));

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registerEquipmentUseCase.execute(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(equipmentRepository, never()).save(any());
    }
}
