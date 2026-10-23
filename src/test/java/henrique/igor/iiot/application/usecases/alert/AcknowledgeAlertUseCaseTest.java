package henrique.igor.iiot.application.usecases.alert;

import henrique.igor.iiot.application.usecases.alert.dto.AcknowledgeAlertInput;
import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AlertRepository;
import henrique.igor.iiot.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcknowledgeAlertUseCaseTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AcknowledgeAlertUseCase acknowledgeAlertUseCase;

    @Test
    @DisplayName("Should acknowledge pending alert successfully")
    void shouldAcknowledgePendingAlertSuccessfully() {
        UUID alertId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(UUID.randomUUID(), "Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, true, OffsetDateTime.now());
        Alert alert = new Alert(alertId, equipment, ruleGroup, Severity.WARNING, "High Temp", AlertStatus.PENDING, null, OffsetDateTime.now(), null, List.of());
        User operator = new User(userId, "Operator", "operator@iiot.com", "hash", "recovery", UserRole.OPERATOR, null, OffsetDateTime.now());

        AcknowledgeAlertInput input = new AcknowledgeAlertInput(alertId, userId);

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(userRepository.findById(userId)).thenReturn(Optional.of(operator));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Alert result = acknowledgeAlertUseCase.execute(input);

        assertNotNull(result);
        assertEquals(AlertStatus.ACKNOWLEDGED, result.getStatus());
        assertEquals(operator, result.getAcknowledgedBy());
        verify(alertRepository, times(1)).save(alert);
    }

    @Test
    @DisplayName("Should throw exception when acknowledging an already resolved alert")
    void shouldThrowExceptionWhenAlertAlreadyResolved() {
        UUID alertId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(UUID.randomUUID(), "Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, true, OffsetDateTime.now());
        Alert resolvedAlert = new Alert(alertId, equipment, ruleGroup, Severity.WARNING, "High Temp", AlertStatus.RESOLVED, null, OffsetDateTime.now(), OffsetDateTime.now(), List.of());
        User operator = new User(userId, "Operator", "operator@iiot.com", "hash", "recovery", UserRole.OPERATOR, null, OffsetDateTime.now());

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(resolvedAlert));
        when(userRepository.findById(userId)).thenReturn(Optional.of(operator));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> acknowledgeAlertUseCase.execute(new AcknowledgeAlertInput(alertId, userId)));

        assertEquals("Cannot acknowledge an already resolved alert.", ex.getMessage());
        verify(alertRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when input or IDs are null")
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(DomainValidationException.class, () -> acknowledgeAlertUseCase.execute(null));
        assertThrows(DomainValidationException.class, () -> acknowledgeAlertUseCase.execute(new AcknowledgeAlertInput(null, UUID.randomUUID())));
        assertThrows(DomainValidationException.class, () -> acknowledgeAlertUseCase.execute(new AcknowledgeAlertInput(UUID.randomUUID(), null)));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when alert or user does not exist")
    void shouldThrowExceptionWhenAlertOrUserNotFound() {
        UUID alertId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> acknowledgeAlertUseCase.execute(new AcknowledgeAlertInput(alertId, userId)));
    }
}
