package henrique.igor.iiot.application.usecases.telemetry;

import henrique.igor.iiot.application.usecases.alert.TriggerAlertUseCase;
import henrique.igor.iiot.application.usecases.alert.dto.TriggerAlertInput;
import henrique.igor.iiot.application.usecases.ruleGroup.EvaluateRuleGroupUseCase;
import henrique.igor.iiot.application.usecases.ruleGroup.dto.EvaluateRuleGroupInput;
import henrique.igor.iiot.application.usecases.ruleGroup.dto.RuleEvaluationResult;
import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.domain.entities.enums.*;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessEquipmentAnomaliesUseCaseTest {

    @Mock
    private RuleGroupRepository ruleGroupRepository;

    @Mock
    private EvaluateRuleGroupUseCase evaluateRuleGroupUseCase;

    @Mock
    private TriggerAlertUseCase triggerAlertUseCase;

    @InjectMocks
    private ProcessEquipmentAnomaliesUseCase processEquipmentAnomaliesUseCase;

    @Test
    @DisplayName("Should trigger alert and return CRITICAL health status when active rule group is breached")
    void shouldTriggerAlertAndReturnCriticalStatusWhenBreached() {
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        Sensor tempSensor = new Sensor(UUID.randomUUID(), "TEM-001", SensorType.TEMPERATURE, SensorStatus.ONLINE, "°C", "topic", equipment, OffsetDateTime.now());
        RuleGroup criticalRule = new RuleGroup(UUID.randomUUID(), "Overheating Rule", "Desc", equipment, Severity.CRITICAL, LogicalOperator.AND, true, OffsetDateTime.now());
        RuleCondition condition = new RuleCondition(criticalRule, tempSensor, ComparisonOperator.GREATER_THAN, new BigDecimal("80.0"));
        AlertSensorSnapshot snapshot = new AlertSensorSnapshot(tempSensor, new BigDecimal("95.0"));

        RuleEvaluationResult evaluationResult = new RuleEvaluationResult(
                criticalRule,
                true,
                Severity.CRITICAL,
                List.of(condition),
                List.of(snapshot)
        );

        when(ruleGroupRepository.findByEquipmentId(equipment.getEquipmentId())).thenReturn(List.of(criticalRule));
        when(evaluateRuleGroupUseCase.execute(any(EvaluateRuleGroupInput.class))).thenReturn(evaluationResult);

        Optional<HealthStatus> status = processEquipmentAnomaliesUseCase.execute(
                equipment,
                Map.of(tempSensor.getSensorId(), new BigDecimal("95.0"))
        );

        assertTrue(status.isPresent());
        assertEquals(HealthStatus.CRITICAL, status.get());
        verify(evaluateRuleGroupUseCase, times(1)).execute(any(EvaluateRuleGroupInput.class));
        verify(triggerAlertUseCase, times(1)).execute(any(TriggerAlertInput.class));
    }

    @Test
    @DisplayName("Should return HEALTH status when active rule group is evaluated and not breached")
    void shouldReturnHealthStatusWhenActiveRuleNotBreached() {
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup ruleGroup = new RuleGroup(UUID.randomUUID(), "Overheating Rule", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, true, OffsetDateTime.now());

        RuleEvaluationResult evaluationResult = new RuleEvaluationResult(
                ruleGroup,
                false,
                Severity.REGULAR,
                List.of(),
                List.of()
        );

        when(ruleGroupRepository.findByEquipmentId(equipment.getEquipmentId())).thenReturn(List.of(ruleGroup));
        when(evaluateRuleGroupUseCase.execute(any(EvaluateRuleGroupInput.class))).thenReturn(evaluationResult);

        Optional<HealthStatus> status = processEquipmentAnomaliesUseCase.execute(
                equipment,
                Map.of(UUID.randomUUID(), new BigDecimal("60.0"))
        );

        assertTrue(status.isPresent());
        assertEquals(HealthStatus.HEALTH, status.get());
        verify(triggerAlertUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Should return empty when equipment has no active rule groups")
    void shouldReturnEmptyWhenNoActiveRuleGroups() {
        Equipment equipment = new Equipment(UUID.randomUUID(), "MTR-USI-001", EquipType.MOTOR, EquipStatus.ACTIVE, null, null, OffsetDateTime.now());
        RuleGroup inactiveRule = new RuleGroup(UUID.randomUUID(), "Inactive Rule", "Desc", equipment, Severity.WARNING, LogicalOperator.AND, false, OffsetDateTime.now());

        when(ruleGroupRepository.findByEquipmentId(equipment.getEquipmentId())).thenReturn(List.of(inactiveRule));

        Optional<HealthStatus> status = processEquipmentAnomaliesUseCase.execute(
                equipment,
                Map.of(UUID.randomUUID(), BigDecimal.TEN)
        );

        assertTrue(status.isEmpty());
        verify(evaluateRuleGroupUseCase, never()).execute(any());
        verify(triggerAlertUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Should throw exception when equipment is null")
    void shouldThrowExceptionWhenEquipmentIsNull() {
        assertThrows(DomainValidationException.class,
                () -> processEquipmentAnomaliesUseCase.execute(null, Collections.emptyMap()));
    }
}
