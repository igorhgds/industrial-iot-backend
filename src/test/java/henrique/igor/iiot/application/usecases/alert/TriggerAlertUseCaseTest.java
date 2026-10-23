package henrique.igor.iiot.application.usecases.alert;

import henrique.igor.iiot.application.usecases.alert.dto.SensorSnapshotInput;
import henrique.igor.iiot.application.usecases.alert.dto.TriggerAlertInput;
import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AlertRepository;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TriggerAlertUseCaseTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private RuleGroupRepository ruleGroupRepository;

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private TriggerAlertUseCase triggerAlertUseCase;

    @Test
    @DisplayName("Should trigger alert and capture sensor snapshots successfully")
    void shouldTriggerAlertSuccessfully() {
        UUID equipmentId = UUID.randomUUID();
        UUID ruleGroupId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();

        Equipment equipment = new Equipment(equipmentId, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Overheating Alert", "Desc", equipment, Severity.CRITICAL, LogicalOperator.AND, true, OffsetDateTime.now());
        Sensor sensor = new Sensor(sensorId, "TEM-001-MTR-USI-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment, OffsetDateTime.now());

        TriggerAlertInput input = new TriggerAlertInput(
                equipmentId,
                ruleGroupId,
                null,
                "Motor temperature exceeded critical threshold",
                List.of(new SensorSnapshotInput(sensorId, new BigDecimal("96.4")))
        );

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensor));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Alert result = triggerAlertUseCase.execute(input);

        assertNotNull(result);
        assertEquals(equipment, result.getEquipment());
        assertEquals(ruleGroup, result.getRuleGroup());
        assertEquals(Severity.CRITICAL, result.getSeverity());
        assertEquals(AlertStatus.PENDING, result.getStatus());
        assertEquals("Motor temperature exceeded critical threshold", result.getMessage());
        assertEquals(1, result.getSensorSnapshots().size());
        assertEquals(sensor, result.getSensorSnapshots().get(0).getSensor());
        assertEquals(new BigDecimal("96.4"), result.getSensorSnapshots().get(0).getValueAtTrigger());

        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    @DisplayName("Should throw exception when input or required fields are missing")
    void shouldThrowExceptionWhenRequiredFieldsMissing() {
        UUID id = UUID.randomUUID();
        List<SensorSnapshotInput> validSnapshots = List.of(new SensorSnapshotInput(id, BigDecimal.TEN));

        assertThrows(DomainValidationException.class, () -> triggerAlertUseCase.execute(null));
        assertThrows(DomainValidationException.class,
                () -> triggerAlertUseCase.execute(new TriggerAlertInput(null, id, Severity.WARNING, "Msg", validSnapshots)));
        assertThrows(DomainValidationException.class,
                () -> triggerAlertUseCase.execute(new TriggerAlertInput(id, null, Severity.WARNING, "Msg", validSnapshots)));
        assertThrows(DomainValidationException.class,
                () -> triggerAlertUseCase.execute(new TriggerAlertInput(id, id, Severity.WARNING, " ", validSnapshots)));
        assertThrows(DomainValidationException.class,
                () -> triggerAlertUseCase.execute(new TriggerAlertInput(id, id, Severity.WARNING, "Msg", Collections.emptyList())));
        verify(alertRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when rule group does not belong to equipment")
    void shouldThrowExceptionWhenRuleGroupBelongsToDifferentEquipment() {
        UUID equipmentId1 = UUID.randomUUID();
        UUID equipmentId2 = UUID.randomUUID();
        UUID ruleGroupId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();

        Equipment equipment1 = new Equipment(equipmentId1, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Equipment equipment2 = new Equipment(equipmentId2, "CMP-USI-002", EquipType.COMPRESSOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Alert", "Desc", equipment2, Severity.WARNING, LogicalOperator.AND, true, OffsetDateTime.now());

        TriggerAlertInput input = new TriggerAlertInput(
                equipmentId1,
                ruleGroupId,
                Severity.WARNING,
                "Anomaly detected",
                List.of(new SensorSnapshotInput(sensorId, BigDecimal.TEN))
        );

        when(equipmentRepository.findById(equipmentId1)).thenReturn(Optional.of(equipment1));
        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> triggerAlertUseCase.execute(input));

        assertEquals("RuleGroup does not belong to the specified Equipment.", ex.getMessage());
        verify(alertRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when sensor does not belong to equipment")
    void shouldThrowExceptionWhenSensorBelongsToDifferentEquipment() {
        UUID equipmentId1 = UUID.randomUUID();
        UUID equipmentId2 = UUID.randomUUID();
        UUID ruleGroupId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();

        Equipment equipment1 = new Equipment(equipmentId1, "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Equipment equipment2 = new Equipment(equipmentId2, "CMP-USI-002", EquipType.COMPRESSOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(ruleGroupId, "Alert", "Desc", equipment1, Severity.WARNING, LogicalOperator.AND, true, OffsetDateTime.now());
        Sensor sensorFromOtherEquip = new Sensor(sensorId, "TEM-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment2, OffsetDateTime.now());

        TriggerAlertInput input = new TriggerAlertInput(
                equipmentId1,
                ruleGroupId,
                Severity.WARNING,
                "Anomaly detected",
                List.of(new SensorSnapshotInput(sensorId, BigDecimal.TEN))
        );

        when(equipmentRepository.findById(equipmentId1)).thenReturn(Optional.of(equipment1));
        when(ruleGroupRepository.findById(ruleGroupId)).thenReturn(Optional.of(ruleGroup));
        when(sensorRepository.findById(sensorId)).thenReturn(Optional.of(sensorFromOtherEquip));

        DomainValidationException ex = assertThrows(DomainValidationException.class,
                () -> triggerAlertUseCase.execute(input));

        assertEquals("Sensor does not belong to the same equipment as the Alert.", ex.getMessage());
        verify(alertRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when equipment, ruleGroup or sensor is not found")
    void shouldThrowResourceNotFoundException() {
        UUID equipmentId = UUID.randomUUID();
        UUID ruleGroupId = UUID.randomUUID();
        UUID sensorId = UUID.randomUUID();

        TriggerAlertInput input = new TriggerAlertInput(
                equipmentId,
                ruleGroupId,
                Severity.WARNING,
                "Anomaly detected",
                List.of(new SensorSnapshotInput(sensorId, BigDecimal.TEN))
        );

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> triggerAlertUseCase.execute(input));
    }
}
