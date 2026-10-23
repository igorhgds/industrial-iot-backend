package henrique.igor.iiot.application.usecases.alert;

import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AlertRepository;
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
class DismissAlertUseCaseTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private DismissAlertUseCase dismissAlertUseCase;

    @Test
    @DisplayName("Should dismiss active alert and set status to RESOLVED with timestamp")
    void shouldDismissAlertSuccessfully() {
        UUID alertId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(UUID.randomUUID(), "Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, true, OffsetDateTime.now());
        Alert activeAlert = new Alert(alertId, equipment, ruleGroup, Severity.WARNING, "High Temp", AlertStatus.ACKNOWLEDGED, null, OffsetDateTime.now(), null, List.of());

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(activeAlert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Alert result = dismissAlertUseCase.execute(alertId);

        assertNotNull(result);
        assertEquals(AlertStatus.RESOLVED, result.getStatus());
        assertNotNull(result.getResolvedAt());
        verify(alertRepository, times(1)).save(activeAlert);
    }

    @Test
    @DisplayName("Should throw exception when dismissing an already resolved alert")
    void shouldThrowExceptionWhenAlertAlreadyResolved() {
        UUID alertId = UUID.randomUUID();
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(UUID.randomUUID(), "Alert", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, true, OffsetDateTime.now());
        Alert resolvedAlert = new Alert(alertId, equipment, ruleGroup, Severity.WARNING, "High Temp", AlertStatus.RESOLVED, null, OffsetDateTime.now(), OffsetDateTime.now(), List.of());

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(resolvedAlert));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> dismissAlertUseCase.execute(alertId));

        assertEquals("Alert is already resolved.", ex.getMessage());
        verify(alertRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when alertId is null")
    void shouldThrowExceptionWhenAlertIdIsNull() {
        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> dismissAlertUseCase.execute(null));

        assertEquals("Alert ID cannot be null.", ex.getMessage());
        verify(alertRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when alert does not exist")
    void shouldThrowExceptionWhenAlertNotFound() {
        UUID alertId = UUID.randomUUID();
        when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dismissAlertUseCase.execute(alertId));
    }
}
