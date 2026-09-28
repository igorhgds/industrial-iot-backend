package henrique.igor.iiot.application.usecases.sector;

import henrique.igor.iiot.application.usecases.sector.dto.UpdateSectorRequest;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.SectorRepository;
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
class UpdateSectorUseCaseTest {

    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private UpdateSectorUseCase updateSectorUseCase;

    @Test
    @DisplayName("Should update sector successfully when name is not changed")
    void shouldUpdateSectorSuccessfullyWhenNameIsNotChanged() {
        // ARRANGE
        UUID sectorId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(10);
        Sector existingSector = new Sector(sectorId, "Machining Line", "Old description", createdAt);

        UpdateSectorRequest request = new UpdateSectorRequest(
                sectorId,
                "Machining Line",
                "Updated description with new details"
        );

        when(sectorRepository.findById(sectorId)).thenReturn(Optional.of(existingSector));
        when(sectorRepository.save(any(Sector.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Sector updatedSector = updateSectorUseCase.execute(request);

        // ASSERT
        assertNotNull(updatedSector);
        assertEquals(sectorId, updatedSector.getSectorId());
        assertEquals("Machining Line", updatedSector.getName());
        assertEquals("Updated description with new details", updatedSector.getDescription());
        assertEquals(createdAt, updatedSector.getCreatedAt());

        verify(sectorRepository, times(1)).findById(sectorId);
        verify(sectorRepository, never()).findByName(anyString());
        verify(sectorRepository, times(1)).save(any(Sector.class));
    }

    @Test
    @DisplayName("Should update sector successfully when name is changed to an available name")
    void shouldUpdateSectorSuccessfullyWhenNameIsChangedToAvailableName() {
        // ARRANGE
        UUID sectorId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusDays(5);
        Sector existingSector = new Sector(sectorId, "Old Line Name", "Description", createdAt);

        UpdateSectorRequest request = new UpdateSectorRequest(
                sectorId,
                "New Line Name",
                "Updated description"
        );

        when(sectorRepository.findById(sectorId)).thenReturn(Optional.of(existingSector));
        when(sectorRepository.findByName("New Line Name")).thenReturn(Optional.empty());
        when(sectorRepository.save(any(Sector.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Sector updatedSector = updateSectorUseCase.execute(request);

        // ASSERT
        assertNotNull(updatedSector);
        assertEquals(sectorId, updatedSector.getSectorId());
        assertEquals("New Line Name", updatedSector.getName());
        assertEquals("Updated description", updatedSector.getDescription());
        assertEquals(createdAt, updatedSector.getCreatedAt());

        verify(sectorRepository, times(1)).findById(sectorId);
        verify(sectorRepository, times(1)).findByName("New Line Name");
        verify(sectorRepository, times(1)).save(any(Sector.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when sector does not exist")
    void shouldThrowExceptionWhenSectorDoesNotExist() {
        // ARRANGE
        UUID nonExistentId = UUID.randomUUID();
        UpdateSectorRequest request = new UpdateSectorRequest(nonExistentId, "Sector Name", "Description");

        when(sectorRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateSectorUseCase.execute(request)
        );

        assertEquals("Sector not found with ID: " + nonExistentId, exception.getMessage());
        verify(sectorRepository, times(1)).findById(nonExistentId);
        verify(sectorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when new name already belongs to another sector")
    void shouldThrowExceptionWhenNewNameAlreadyExists() {
        // ARRANGE
        UUID sectorId = UUID.randomUUID();
        Sector existingSector = new Sector(sectorId, "Line A", "Description A", OffsetDateTime.now());
        Sector anotherSector = new Sector(UUID.randomUUID(), "Line B", "Description B", OffsetDateTime.now());

        UpdateSectorRequest request = new UpdateSectorRequest(
                sectorId,
                "Line B",
                "New Description"
        );

        when(sectorRepository.findById(sectorId)).thenReturn(Optional.of(existingSector));
        when(sectorRepository.findByName("Line B")).thenReturn(Optional.of(anotherSector));

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateSectorUseCase.execute(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(sectorRepository, times(1)).findById(sectorId);
        verify(sectorRepository, times(1)).findByName("Line B");
        verify(sectorRepository, never()).save(any());
    }
}