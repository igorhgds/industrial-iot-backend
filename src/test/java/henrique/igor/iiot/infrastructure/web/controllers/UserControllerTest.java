package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.user.CreateUserUseCase;
import henrique.igor.iiot.application.usecases.user.GetUserByIdUseCase;
import henrique.igor.iiot.application.usecases.user.ListUserUseCase;
import henrique.igor.iiot.application.usecases.user.UpdateUserUseCase;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.entities.enums.UserRole;
import henrique.igor.iiot.domain.exceptions.InvalidRoleAssignmentException;
import henrique.igor.iiot.domain.exceptions.ResourceAlreadyExistsException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.infrastructure.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private GetUserByIdUseCase getUserByIdUseCase;

    @Mock
    private ListUserUseCase listUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/users should return 201 Created when payload is valid")
    void shouldCreateUserAndReturn201() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID sectorId = UUID.randomUUID();
        Sector sector = new Sector(sectorId, "Usinagem", "Setor CNC", OffsetDateTime.now());
        User createdUser = new User(
                userId,
                "Carlos Mendes",
                "carlos@industry.com",
                "secret",
                "REC12345",
                UserRole.TECHNICIAN,
                sector,
                OffsetDateTime.now()
        );

        when(createUserUseCase.execute(any())).thenReturn(createdUser);

        String requestJson = """
                {
                  "name": "Carlos Mendes",
                  "email": "carlos@industry.com",
                  "password": "secret",
                  "passwordRecovery": "REC12345",
                  "userRole": "TECHNICIAN",
                  "sectorId": "%s"
                }
                """.formatted(sectorId);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Carlos Mendes"))
                .andExpect(jsonPath("$.email").value("carlos@industry.com"))
                .andExpect(jsonPath("$.userRole").value("TECHNICIAN"))
                .andExpect(jsonPath("$.sectorId").value(sectorId.toString()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/v1/users should return 400 Bad Request when Bean Validation fails")
    void shouldReturn400WhenValidationFails() throws Exception {
        String invalidJson = """
                {
                  "name": "",
                  "email": "not-an-email",
                  "password": ""
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} should return 404 Not Found when ResourceNotFoundException is thrown")
    void shouldReturn404WhenUserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(getUserByIdUseCase.execute(userId))
                .thenThrow(new ResourceNotFoundException("User", userId));

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/v1/users should return 409 Conflict when ResourceAlreadyExistsException is thrown")
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(createUserUseCase.execute(any()))
                .thenThrow(new ResourceAlreadyExistsException("User", "email", "carlos@industry.com"));

        String requestJson = """
                {
                  "name": "Carlos Mendes",
                  "email": "carlos@industry.com",
                  "password": "secret",
                  "userRole": "TECHNICIAN"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} should return 422 Unprocessable Content on InvalidRoleAssignmentException")
    void shouldReturn422WhenUnprocessableDomainExceptionThrown() throws Exception {
        UUID userId = UUID.randomUUID();
        when(updateUserUseCase.execute(any()))
                .thenThrow(new InvalidRoleAssignmentException("Invalid role assignment"));

        String requestJson = """
                {
                  "name": "Carlos Mendes",
                  "email": "carlos@industry.com",
                  "userRole": "OPERATOR"
                }
                """;

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("GET /api/v1/users should return 200 OK with list of users")
    void shouldReturn200WithUsersList() throws Exception {
        User user = new User(
                UUID.randomUUID(),
                "Ana Souza",
                "ana@industry.com",
                "secret",
                null,
                UserRole.MANAGER,
                null,
                OffsetDateTime.now()
        );
        when(listUserUseCase.execute()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ana Souza"))
                .andExpect(jsonPath("$[0].userRole").value("MANAGER"));
    }
}
