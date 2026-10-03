package henrique.igor.iiot.application.usecases.gateway;

import henrique.igor.iiot.application.usecases.gateway.dto.RegisterGatewayInput;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.enums.GatewayStatus;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import henrique.igor.iiot.domain.services.GatewayCodeGenerator;
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
class RegisterGatewayUseCaseTest {

    @Mock
    private GatewayRepository gatewayRepository;

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private GatewayCodeGenerator gatewayCodeGenerator;

    @InjectMocks
    private RegisterGatewayUseCase registerGatewayUseCase;

    @Test
    @DisplayName("Should register gateway successfully when sector is provided and valid")
    void shouldRegisterGatewaySuccessfullyWithSector() {
        // ARRANGE
        UUID sectorId = UUID.randomUUID();
        Sector sector = new Sector(sectorId, "Stamping Line", "Description", null);

        RegisterGatewayInput request = new RegisterGatewayInput(
                "GW-ST-01",
                "AA:BB:CC:DD:EE:01",
                "192.168.1.100",
                "v1.0.0",
                GatewayStatus.ONLINE,
                sectorId
        );

        when(gatewayRepository.findByCode(request.code())).thenReturn(Optional.empty());
        when(sectorRepository.findById(sectorId)).thenReturn(Optional.of(sector));
        when(gatewayRepository.save(any(Gateway.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Gateway createdGateway = registerGatewayUseCase.execute(request);

        // ASSERT
        assertNotNull(createdGateway);
        assertNotNull(createdGateway.getGatewayId());
        assertEquals("GW-ST-01", createdGateway.getCode());
        assertEquals("AA:BB:CC:DD:EE:01", createdGateway.getMacAddress());
        assertEquals("192.168.1.100", createdGateway.getIpAddress());
        assertEquals("v1.0.0", createdGateway.getFirmwareVersion());
        assertEquals(GatewayStatus.ONLINE, createdGateway.getStatus());
        assertEquals(sector, createdGateway.getSector());

        verify(gatewayRepository, times(1)).findByCode(request.code());
        verify(sectorRepository, times(1)).findById(sectorId);
        verify(gatewayRepository, times(1)).save(any(Gateway.class));
    }

    @Test
    @DisplayName("Should register gateway successfully when sectorId is null")
    void shouldRegisterGatewaySuccessfullyWithoutSector() {
        // ARRANGE
        RegisterGatewayInput request = new RegisterGatewayInput(
                "GW-STANDALONE",
                "AA:BB:CC:DD:EE:02",
                "192.168.1.101",
                "v1.0.0",
                GatewayStatus.OFFLINE,
                null
        );

        when(gatewayRepository.findByCode(request.code())).thenReturn(Optional.empty());
        when(gatewayRepository.save(any(Gateway.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Gateway createdGateway = registerGatewayUseCase.execute(request);

        // ASSERT
        assertNotNull(createdGateway);
        assertEquals("GW-STANDALONE", createdGateway.getCode());
        assertNull(createdGateway.getSector());

        verify(gatewayRepository, times(1)).findByCode(request.code());
        verify(sectorRepository, never()).findById(any());
        verify(gatewayRepository, times(1)).save(any(Gateway.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when gateway code already exists")
    void shouldThrowExceptionWhenCodeAlreadyExists() {
        // ARRANGE
        RegisterGatewayInput request = new RegisterGatewayInput(
                "GW-DUPLICATE",
                "AA:BB:CC:DD:EE:03",
                "192.168.1.102",
                "v1.0.0",
                GatewayStatus.ONLINE,
                null
        );
        Gateway existingGateway = new Gateway("GW-DUPLICATE", "AA:BB:CC:DD:EE:03", "192.168.1.102", "v1.0.0", GatewayStatus.ONLINE, null, null);

        when(gatewayRepository.findByCode(request.code())).thenReturn(Optional.of(existingGateway));

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registerGatewayUseCase.execute(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(gatewayRepository, times(1)).findByCode(request.code());
        verify(gatewayRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when sectorId is not found")
    void shouldThrowExceptionWhenSectorNotFound() {
        // ARRANGE
        UUID nonExistentSectorId = UUID.randomUUID();
        RegisterGatewayInput request = new RegisterGatewayInput(
                "GW-INVALID-SECTOR",
                "AA:BB:CC:DD:EE:04",
                "192.168.1.103",
                "v1.0.0",
                GatewayStatus.OFFLINE,
                nonExistentSectorId
        );

        when(sectorRepository.findById(nonExistentSectorId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> registerGatewayUseCase.execute(request)
        );

        assertTrue(exception.getMessage().contains("Sector not found with ID: " + nonExistentSectorId));
        verify(sectorRepository, times(1)).findById(nonExistentSectorId);
        verify(gatewayRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should auto-generate gateway code when code is null")
    void shouldAutoGenerateGatewayCodeWhenCodeIsNull() {
        // 1. ARRANGE
        UUID sectorId = UUID.randomUUID();
        Sector sector = new Sector(sectorId, "Usinagem", "Sector Usinagem", null);

        RegisterGatewayInput request = new RegisterGatewayInput(
                null, // Code nulo -> aciona geração automática
                "AA:BB:CC:DD:EE:05",
                "192.168.1.105",
                "v1.0.0",
                GatewayStatus.ONLINE,
                sectorId
        );

        when(sectorRepository.findById(sectorId)).thenReturn(Optional.of(sector));
        when(gatewayCodeGenerator.generate(sector)).thenReturn("GW-USI-003");
        when(gatewayRepository.save(any(Gateway.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. ACT
        Gateway createdGateway = registerGatewayUseCase.execute(request);

        // 3. ASSERT
        assertNotNull(createdGateway);
        assertEquals("GW-USI-003", createdGateway.getCode());
        assertEquals(sector, createdGateway.getSector());

        verify(sectorRepository, times(1)).findById(sectorId);
        verify(gatewayCodeGenerator, times(1)).generate(sector);
        verify(gatewayRepository, never()).findByCode(any());
        verify(gatewayRepository, times(1)).save(any(Gateway.class));
    }
}
