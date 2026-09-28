package henrique.igor.iiot.application.usecases.sector;

import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListSectorUseCaseTest {

    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private ListSectorUseCase listSectorUseCase;

    @Test
    @DisplayName("Should return list of sectors when sectors exists")
    void shouldReturnListOfSectorsWhenSectorsExist(){
        // Arrange
        Sector sector1 = new Sector(UUID.randomUUID(), "sector one", "description one", null);
        Sector sector2 = new Sector(UUID.randomUUID(), "sector two", "description two", null);
        List<Sector> expectedSectors = List.of(sector1, sector2);

        when(sectorRepository.findAll()).thenReturn(expectedSectors);

        // Act
        List<Sector> actualSectors = listSectorUseCase.execute();

        // Assert
        assertNotNull(actualSectors);
        assertEquals(2, actualSectors.size());
        assertEquals("sector one", actualSectors.get(0).getName());
        assertEquals("sector two", actualSectors.get(1).getName());
    }

    @Test
    @DisplayName("Should return empty list when no sectors exist")
    void shouldReturnEmptyListWhenNoSectorsExist() {
        // ARRANGE
        when(sectorRepository.findAll()).thenReturn(Collections.emptyList());

        // ACT
        List<Sector> actualSector = listSectorUseCase.execute();

        // ASSERT
        assertNotNull(actualSector);
        assertTrue(actualSector.isEmpty());

        verify(sectorRepository, times(1)).findAll();
    }

}