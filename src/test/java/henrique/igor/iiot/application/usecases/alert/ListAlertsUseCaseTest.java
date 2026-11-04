package henrique.igor.iiot.application.usecases.alert;

import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.domain.repositories.AlertRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListAlertsUseCaseTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private ListAlertsUseCase listAlertsUseCase;

    @Test
    @DisplayName("Should return all alerts when no filters are provided")
    void shouldReturnAllAlertsWhenNoFiltersProvided() {
        Alert alert = new Alert(UUID.randomUUID(), null, null, Severity.CRITICAL, "Overheating", AlertStatus.PENDING, null, OffsetDateTime.now(), null, List.of());
        when(alertRepository.findAll()).thenReturn(List.of(alert));

        List<Alert> result = listAlertsUseCase.execute(null, null);

        assertEquals(1, result.size());
        verify(alertRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should filter alerts by status when only status is provided")
    void shouldFilterByStatusWhenOnlyStatusProvided() {
        Alert alert = new Alert(UUID.randomUUID(), null, null, Severity.WARNING, "Vibration high", AlertStatus.PENDING, null, OffsetDateTime.now(), null, List.of());
        when(alertRepository.findByStatus(AlertStatus.PENDING)).thenReturn(List.of(alert));

        List<Alert> result = listAlertsUseCase.execute(null, AlertStatus.PENDING);

        assertEquals(1, result.size());
        verify(alertRepository, times(1)).findByStatus(AlertStatus.PENDING);
    }

    @Test
    @DisplayName("Should filter alerts by equipmentId and status when both are provided")
    void shouldFilterByEquipmentAndStatus() {
        UUID equipmentId = UUID.randomUUID();
        Alert pendingAlert = new Alert(UUID.randomUUID(), null, null, Severity.WARNING, "Pending", AlertStatus.PENDING, null, OffsetDateTime.now(), null, List.of());
        Alert resolvedAlert = new Alert(UUID.randomUUID(), null, null, Severity.WARNING, "Resolved", AlertStatus.RESOLVED, null, OffsetDateTime.now(), OffsetDateTime.now(), List.of());

        when(alertRepository.findByEquipmentId(equipmentId)).thenReturn(List.of(pendingAlert, resolvedAlert));

        List<Alert> result = listAlertsUseCase.execute(equipmentId, AlertStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(AlertStatus.PENDING, result.get(0).getStatus());
        verify(alertRepository, times(1)).findByEquipmentId(equipmentId);
    }
}
