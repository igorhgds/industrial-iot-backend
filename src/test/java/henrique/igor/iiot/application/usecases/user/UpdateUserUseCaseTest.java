package henrique.igor.iiot.application.usecases.user;

import henrique.igor.iiot.application.usecases.user.dto.UpdateUserRequest;
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
class UpdateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    @Test
    @DisplayName("Should update user successfully when email is not changed")
    void shouldUpdateUserSuccessfullyWhenEmailNotChanged() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        UUID sectorId = UUID.randomUUID();
        Sector sector = new Sector(sectorId, "Assembly Line", "Description", null);
        User existingUser = new User(userId, "Old Name", "user@example.com", "pass", "hint", UserRole.OPERATOR, null, null);

        UpdateUserRequest request = new UpdateUserRequest(
                userId,
                "New Name",
                "user@example.com",
                UserRole.TECHNICIAN,
                sectorId
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(sectorRepository.findById(sectorId)).thenReturn(Optional.of(sector));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        User updatedUser = updateUserUseCase.execute(request);

        // ASSERT
        assertNotNull(updatedUser);
        assertEquals("New Name", updatedUser.getName());
        assertEquals("user@example.com", updatedUser.getEmail());
        assertEquals(UserRole.TECHNICIAN, updatedUser.getUserRole());
        assertEquals(sector, updatedUser.getSector());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findByEmail(anyString());
        verify(sectorRepository, times(1)).findById(sectorId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully when email is changed to an available email")
    void shouldUpdateUserSuccessfullyWhenEmailChangedToAvailable() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        User existingUser = new User(userId, "User", "old@example.com", "pass", "hint", UserRole.OPERATOR, null, null);

        UpdateUserRequest request = new UpdateUserRequest(
                userId,
                "Updated User",
                "new@example.com",
                UserRole.MANAGER,
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        User updatedUser = updateUserUseCase.execute(request);

        // ASSERT
        assertNotNull(updatedUser);
        assertEquals("Updated User", updatedUser.getName());
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals(UserRole.MANAGER, updatedUser.getUserRole());
        assertNull(updatedUser.getSector());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail("new@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {
        // ARRANGE
        UUID nonExistentUserId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest(nonExistentUserId, "Name", "email@example.com", UserRole.OPERATOR, null);

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateUserUseCase.execute(request)
        );

        assertEquals("User not found with ID: " + nonExistentUserId, exception.getMessage());
        verify(userRepository, times(1)).findById(nonExistentUserId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when new email already belongs to another user")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        User existingUser = new User(userId, "User 1", "user1@example.com", "pass", "hint", UserRole.OPERATOR, null, null);
        User anotherUser = new User(UUID.randomUUID(), "User 2", "user2@example.com", "pass", "hint", UserRole.OPERATOR, null, null);

        UpdateUserRequest request = new UpdateUserRequest(
                userId,
                "User 1 Updated",
                "user2@example.com",
                UserRole.OPERATOR,
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(anotherUser));

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateUserUseCase.execute(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail("user2@example.com");
        verify(userRepository, never()).save(any());
    }
}
