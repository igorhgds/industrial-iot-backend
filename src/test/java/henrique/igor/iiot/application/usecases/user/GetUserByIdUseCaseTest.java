package henrique.igor.iiot.application.usecases.user;

import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.entities.enums.UserRole;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByIdUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserByIdUseCase getUserByIdUseCase;

    @Test
    @DisplayName("Should get user successfully when ID exists")
    void shouldGetUserSuccessfullyById() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        User expectedUser = new User(
                userId,
                "Igor Henrique",
                "igor@example.com",
                "password123",
                "recoveryHint",
                UserRole.ADMIN,
                null,
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // ACT
        User actualUser = getUserByIdUseCase.execute(userId);

        // ASSERT
        assertNotNull(actualUser);
        assertEquals(userId, actualUser.getUserId());
        assertEquals("Igor Henrique", actualUser.getName());
        assertEquals("igor@example.com", actualUser.getEmail());
        assertEquals(UserRole.ADMIN, actualUser.getUserRole());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when userId is null")
    void shouldThrowExceptionWhenUserIdIsNull() {
        // ARRANGE
        UUID nullUserId = null;

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getUserByIdUseCase.execute(nullUserId)
        );

        assertEquals("User ID cannot be null.", exception.getMessage());

        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user is not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // ARRANGE
        UUID nonExistentUserId = UUID.randomUUID();

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getUserByIdUseCase.execute(nonExistentUserId)
        );

        assertEquals("User not found with ID: " + nonExistentUserId, exception.getMessage());
        verify(userRepository, times(1)).findById(nonExistentUserId);
    }
}