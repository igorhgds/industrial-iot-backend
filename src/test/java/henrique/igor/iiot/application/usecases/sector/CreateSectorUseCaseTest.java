package henrique.igor.iiot.application.usecases.sector;

import henrique.igor.iiot.application.usecases.sector.dto.CreateSectorInput;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSectorUseCaseTest {

    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private CreateSectorUseCase createSectorUseCase;

    @Test
    @DisplayName("Should create sector successfully")
    void shouldCreateSectorSuccessfully(){
        // Arrange
        CreateSectorInput request = new CreateSectorInput(
                "machining line",
                "A machining line is a sequential arrangement of machine tools—such as CNC mills, lathes, and drills"
        );

        when(sectorRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(sectorRepository.save(any(Sector.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Sector createdSector = createSectorUseCase.execute(request);

        // Assert
        assertNotNull(createdSector);
        assertNotNull(createdSector.getSectorId());
        assertEquals("machining line", createdSector.getName());
        assertEquals("A machining line is a sequential arrangement of machine tools—such as CNC mills, lathes, and drills", createdSector.getDescription());

        verify(sectorRepository, times(1)).findByName(request.name());
        verify(sectorRepository, times(1)).save(any(Sector.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when name already exists")
    void shouldThrowExceptionWhenNameAlreadyExists(){
        // Arrange
        CreateSectorInput request = new CreateSectorInput(
                "machining line",
                "A machining line is a sequential arrangement of machine tools—such as CNC mills, lathes, and drills"
        );

        Sector existingSector = new Sector("machining line", "description");

        when(sectorRepository.findByName(request.name())).thenReturn(Optional.of(existingSector));

        //Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> createSectorUseCase.execute(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(sectorRepository, times(1)).findByName(request.name());
        verify(sectorRepository, never()).save(any());
    }

}