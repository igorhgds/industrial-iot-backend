package henrique.igor.iiot.application.usecases.user;

import henrique.igor.iiot.application.usecases.user.dto.CreateUserRequest;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.entities.enums.UserRole;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import henrique.igor.iiot.domain.repositories.UserRepository;
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
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Test
    @DisplayName("Should create user successfully when sector is provided and valid")
    void shouldCreateUserSuccessfullyWithSector() {
        // Arrange
        UUID sectorId = UUID.randomUUID();
        Sector sector = new Sector(sectorId, "Stamping Line", "Main stamping sector", null);
        CreateUserRequest request = new CreateUserRequest(
                "Igor Henrique",
                "igor@example.com",
                "securePass123",
                "recoveryHint",
                UserRole.ADMIN,
                sectorId
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(sectorRepository.findById(sectorId)).thenReturn(Optional.of(sector));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User createdUser = createUserUseCase.execute(request);

        // Assert
        assertNotNull(createdUser);
        assertNotNull(createdUser.getUserId());
        assertEquals("Igor Henrique", createdUser.getName());
        assertEquals("igor@example.com", createdUser.getEmail());
        assertEquals(UserRole.ADMIN, createdUser.getUserRole());
        assertEquals(sector, createdUser.getSector());

        verify(userRepository, times(1)).findByEmail(request.email());
        verify(sectorRepository, times(1)).findById(sectorId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should create user successfully when sectorId is null")
    void shouldCreateUserSuccessfullyWithoutSector() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "John Doe",
                "john@example.com",
                "pass123",
                "recovery",
                UserRole.OPERATOR,
                null
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User createdUser = createUserUseCase.execute(request);

        // Assert
        assertNotNull(createdUser);
        assertEquals("John Doe", createdUser.getName());
        assertNull(createdUser.getSector());

        verify(userRepository, times(1)).findByEmail(request.email());
        verify(sectorRepository, never()).findById(any());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Duplicate User",
                "existing@example.com",
                "pass123",
                "recovery",
                UserRole.TECHNICIAN,
                null
        );
        User existingUser = new User("Existing", "existing@example.com", "pass", "hint", UserRole.TECHNICIAN, null);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createUserUseCase.execute(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, times(1)).findByEmail(request.email());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when sectorId does not exist")
    void shouldThrowExceptionWhenSectorNotFound() {
        // Arrange
        UUID nonExistentSectorId = UUID.randomUUID();
        CreateUserRequest request = new CreateUserRequest(
                "Test User",
                "test@example.com",
                "pass123",
                "recovery",
                UserRole.OPERATOR,
                nonExistentSectorId
        );

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(sectorRepository.findById(nonExistentSectorId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createUserUseCase.execute(request)
        );

        assertTrue(exception.getMessage().contains("Sector not found with ID: " + nonExistentSectorId));
        verify(sectorRepository, times(1)).findById(nonExistentSectorId);
        verify(userRepository, never()).save(any());
    }
}
