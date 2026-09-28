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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListUserUseCase listUserUseCase;

    @Test
    @DisplayName("Should return list of users when users exist")
    void shouldReturnListOfUsersWhenUsersExist() {
        // ARRANGE
        User user1 = new User(UUID.randomUUID(), "User One", "user1@example.com", "pass1", "hint1", UserRole.OPERATOR, null, null);
        User user2 = new User(UUID.randomUUID(), "User Two", "user2@example.com", "pass2", "hint2", UserRole.TECHNICIAN, null, null);
        List<User> expectedUsers = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        // ACT
        List<User> actualUsers = listUserUseCase.execute();

        // ASSERT
        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertEquals("User One", actualUsers.get(0).getName());
        assertEquals("User Two", actualUsers.get(1).getName());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyListWhenNoUsersExist() {
        // ARRANGE
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // ACT
        List<User> actualUsers = listUserUseCase.execute();

        // ASSERT
        assertNotNull(actualUsers);
        assertTrue(actualUsers.isEmpty());

        verify(userRepository, times(1)).findAll();
    }
}
