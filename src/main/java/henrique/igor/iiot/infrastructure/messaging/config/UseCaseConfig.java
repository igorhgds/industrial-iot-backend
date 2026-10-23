package henrique.igor.iiot.infrastructure.messaging.config;

import henrique.igor.iiot.application.usecases.alert.TriggerAlertUseCase;
import henrique.igor.iiot.application.usecases.ruleGroup.EvaluateRuleGroupUseCase;
import henrique.igor.iiot.application.usecases.telemetry.ProcessEquipmentAnomaliesUseCase;
import henrique.igor.iiot.application.usecases.telemetry.ProcessTelemetryIngestionUseCase;
import henrique.igor.iiot.domain.repositories.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public EvaluateRuleGroupUseCase evaluateRuleGroupUseCase(
            RuleGroupRepository ruleGroupRepository,
            RuleConditionRepository ruleConditionRepository
    ) {
        return new EvaluateRuleGroupUseCase(ruleGroupRepository, ruleConditionRepository);
    }

    @Bean
    public TriggerAlertUseCase triggerAlertUseCase(
            AlertRepository alertRepository,
            EquipmentRepository equipmentRepository,
            RuleGroupRepository ruleGroupRepository,
            SensorRepository sensorRepository
    ) {
        return new TriggerAlertUseCase(
                alertRepository,
                equipmentRepository,
                ruleGroupRepository,
                sensorRepository
        );
    }

    @Bean
    public ProcessEquipmentAnomaliesUseCase processEquipmentAnomaliesUseCase(
            RuleGroupRepository ruleGroupRepository,
            EvaluateRuleGroupUseCase evaluateRuleGroupUseCase,
            TriggerAlertUseCase triggerAlertUseCase
    ) {
        return new ProcessEquipmentAnomaliesUseCase(
                ruleGroupRepository,
                evaluateRuleGroupUseCase,
                triggerAlertUseCase
        );
    }

    @Bean
    public ProcessTelemetryIngestionUseCase processTelemetryIngestionUseCase(
            EquipmentRepository equipmentRepository,
            GatewayRepository gatewayRepository,
            SensorRepository sensorRepository,
            TelemetryDataRepository telemetryDataRepository,
            EquipmentStateRepository equipmentStateRepository,
            ProcessEquipmentAnomaliesUseCase processEquipmentAnomaliesUseCase
    ) {
        return new ProcessTelemetryIngestionUseCase(
                equipmentRepository,
                gatewayRepository,
                sensorRepository,
                telemetryDataRepository,
                equipmentStateRepository,
                processEquipmentAnomaliesUseCase
        );
    }
}
