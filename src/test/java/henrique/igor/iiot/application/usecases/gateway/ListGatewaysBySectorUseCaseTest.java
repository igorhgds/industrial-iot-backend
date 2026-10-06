package henrique.igor.iiot.application.usecases.gateway;

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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListGatewaysBySectorUseCaseTest {

    @Mock
    private GatewayRepository gatewayRepository;

    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private ListGatewaysBySectorUseCase listGatewaysBySectorUseCase;

    @Test
    @DisplayName("Should return list of gateways by sector ID when sector exists")
    void shouldReturnGatewaysBySectorIdWhenSectorExists() {
        // ARRANGE
        UUID sectorId = UUID.randomUUID();
        Sector sector = new Sector(sectorId, "Stamping Sector", "Description", null);
        Gateway gw1 = new Gateway(UUID.randomUUID(), "GW-01", "MAC-1", "192.168.1.1", "v1", GatewayStatus.ONLINE, sector, null, null);
        Gateway gw2 = new Gateway(UUID.randomUUID(), "GW-02", "MAC-2", "192.168.1.2", "v1", GatewayStatus.ONLINE, sector, null, null);

        when(sectorRepository.findById(sectorId)).thenReturn(Optional.of(sector));
        when(gatewayRepository.findBySectorId(sectorId)).thenReturn(List.of(gw1, gw2));

        // ACT
        List<Gateway> actualGateways = listGatewaysBySectorUseCase.execute(sectorId);

        // ASSERT
        assertNotNull(actualGateways);
        assertEquals(2, actualGateways.size());
        assertEquals("GW-01", actualGateways.get(0).getGatewayCode());
        assertEquals("GW-02", actualGateways.get(1).getGatewayCode());

        verify(sectorRepository, times(1)).findById(sectorId);
        verify(gatewayRepository, times(1)).findBySectorId(sectorId);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when sectorId is null")
    void shouldThrowExceptionWhenSectorIdIsNull() {
        // ARRANGE
        UUID nullSectorId = null;

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> listGatewaysBySectorUseCase.execute(nullSectorId)
        );

        assertEquals("Sector ID cannot be null.", exception.getMessage());
        verify(sectorRepository, never()).findById(any());
        verify(gatewayRepository, never()).findBySectorId(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when sector is not found")
    void shouldThrowExceptionWhenSectorNotFound() {
        // ARRANGE
        UUID nonExistentSectorId = UUID.randomUUID();

        when(sectorRepository.findById(nonExistentSectorId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> listGatewaysBySectorUseCase.execute(nonExistentSectorId)
        );

        assertTrue(exception.getMessage().contains("Sector not found with ID: " + nonExistentSectorId));
        verify(sectorRepository, times(1)).findById(nonExistentSectorId);
        verify(gatewayRepository, never()).findBySectorId(any());
    }
}
