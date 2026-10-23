package henrique.igor.iiot.application.usecases.ruleGroup.dto;

import henrique.igor.iiot.domain.entities.AlertSensorSnapshot;
import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.enums.Severity;

import java.util.List;

public record RuleEvaluationResult(
        RuleGroup ruleGroup,
        boolean triggered,
        Severity calculatedSeverity,
        List<RuleCondition> breachedConditions,
        List<AlertSensorSnapshot> sensorSnapshots
) {
}
