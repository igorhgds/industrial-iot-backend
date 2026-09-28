package henrique.igor.iiot.application.usecases.gateway;

import henrique.igor.iiot.application.usecases.gateway.dto.UpdateGatewayRequest;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.enums.GatewayStatus;
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
class UpdateGatewayUseCaseTest {

    @Mock
    private GatewayRepository gatewayRepository;

    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private UpdateGatewayUseCase updateGatewayUseCase;

    @Test
    @DisplayName("Should update gateway successfully using Rich Domain Model methods")
    void shouldUpdateGatewaySuccessfully() {
        // 1. ARRANGE
        UUID gatewayId = UUID.randomUUID();
        UUID newSectorId = UUID.randomUUID();
        Sector oldSector = new Sector(UUID.randomUUID(), "Old Sector", "Desc", null);
        Sector newSector = new Sector(newSectorId, "New Sector", "Desc", null);

        Gateway existingGateway = new Gateway(gatewayId, "GW-01", "AA:BB:CC:DD:EE:FF", "192.168.1.1", "v1.0.0", GatewayStatus.OFFLINE, oldSector, null, null);

        UpdateGatewayRequest request = new UpdateGatewayRequest(
                gatewayId,
                "192.168.1.200", // Novo IP
                "v2.0.0",        // Nova versão
                GatewayStatus.ONLINE, // Novo status
                newSectorId,       // Novo setor
                null
        );

        when(gatewayRepository.findById(gatewayId)).thenReturn(Optional.of(existingGateway));
        when(sectorRepository.findById(newSectorId)).thenReturn(Optional.of(newSector));
        when(gatewayRepository.save(any(Gateway.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. ACT
        Gateway updatedGateway = updateGatewayUseCase.execute(request);

        // 3. ASSERT
        assertNotNull(updatedGateway);
        assertEquals(gatewayId, updatedGateway.getGatewayId());
        assertEquals("192.168.1.200", updatedGateway.getIpAddress());
        assertEquals("v2.0.0", updatedGateway.getFirmwareVersion());
        assertEquals(GatewayStatus.ONLINE, updatedGateway.getStatus());
        assertEquals(newSector, updatedGateway.getSector());

        verify(gatewayRepository, times(1)).findById(gatewayId);
        verify(sectorRepository, times(1)).findById(newSectorId);
        verify(gatewayRepository, times(1)).save(any(Gateway.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when gateway does not exist")
    void shouldThrowExceptionWhenGatewayNotFound() {
        // 1. ARRANGE
        UUID nonExistentId = UUID.randomUUID();
        UpdateGatewayRequest request = new UpdateGatewayRequest(nonExistentId, "192.168.1.2", "v1.1", GatewayStatus.ONLINE, null, null);

        when(gatewayRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // 2. ACT & 3. ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateGatewayUseCase.execute(request)
        );

        assertEquals("Gateway not found with ID: " + nonExistentId, exception.getMessage());
        verify(gatewayRepository, times(1)).findById(nonExistentId);
        verify(gatewayRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when new sector is not found")
    void shouldThrowExceptionWhenNewSectorNotFound() {
        // 1. ARRANGE
        UUID gatewayId = UUID.randomUUID();
        UUID invalidSectorId = UUID.randomUUID();
        Gateway existingGateway = new Gateway(gatewayId, "GW-01", "AA:BB:CC:DD:EE:FF", "192.168.1.1", "v1.0.0", GatewayStatus.OFFLINE, null, null, null);

        UpdateGatewayRequest request = new UpdateGatewayRequest(gatewayId, null, null, null, invalidSectorId, null);

        when(gatewayRepository.findById(gatewayId)).thenReturn(Optional.of(existingGateway));
        when(sectorRepository.findById(invalidSectorId)).thenReturn(Optional.empty());

        // 2. ACT & 3. ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateGatewayUseCase.execute(request)
        );

        assertTrue(exception.getMessage().contains("Sector not found with ID: " + invalidSectorId));
        verify(sectorRepository, times(1)).findById(invalidSectorId);
        verify(gatewayRepository, never()).save(any());
    }
}
