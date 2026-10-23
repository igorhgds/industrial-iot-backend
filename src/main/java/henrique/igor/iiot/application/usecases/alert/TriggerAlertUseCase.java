package henrique.igor.iiot.application.usecases.alert;

import henrique.igor.iiot.application.usecases.alert.dto.SensorSnapshotInput;
import henrique.igor.iiot.application.usecases.alert.dto.TriggerAlertInput;
import henrique.igor.iiot.domain.entities.*;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AlertRepository;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;

import java.util.ArrayList;
import java.util.List;

public class TriggerAlertUseCase {

    private final AlertRepository alertRepository;
    private final EquipmentRepository equipmentRepository;
    private final RuleGroupRepository ruleGroupRepository;
    private final SensorRepository sensorRepository;

    public TriggerAlertUseCase(
            AlertRepository alertRepository,
            EquipmentRepository equipmentRepository,
            RuleGroupRepository ruleGroupRepository,
            SensorRepository sensorRepository
    ) {
        this.alertRepository = alertRepository;
        this.equipmentRepository = equipmentRepository;
        this.ruleGroupRepository = ruleGroupRepository;
        this.sensorRepository = sensorRepository;
    }

    public Alert execute(TriggerAlertInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.equipmentId() == null) {
            throw new DomainValidationException("Equipment ID cannot be null.");
        }
        if (input.ruleGroupId() == null) {
            throw new DomainValidationException("RuleGroup ID cannot be null.");
        }
        if (input.message() == null || input.message().isBlank()) {
            throw new DomainValidationException("Alert message cannot be null or blank.");
        }
        if (input.sensorSnapshots() == null || input.sensorSnapshots().isEmpty()) {
            throw new DomainValidationException("At least one sensor snapshot is required to trigger an alert.");
        }

        Equipment equipment = equipmentRepository.findById(input.equipmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", input.equipmentId()));

        RuleGroup ruleGroup = ruleGroupRepository.findById(input.ruleGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("RuleGroup", input.ruleGroupId()));

        if (ruleGroup.getEquipment() == null ||
                !ruleGroup.getEquipment().getEquipmentId().equals(equipment.getEquipmentId())) {
            throw new DomainValidationException("RuleGroup does not belong to the specified Equipment.");
        }

        List<AlertSensorSnapshot> snapshots = new ArrayList<>();
        for (SensorSnapshotInput snapshotInput : input.sensorSnapshots()) {
            if (snapshotInput == null || snapshotInput.sensorId() == null || snapshotInput.valueAtTrigger() == null) {
                throw new DomainValidationException("Sensor snapshot must contain a valid sensor ID and value.");
            }

            Sensor sensor = sensorRepository.findById(snapshotInput.sensorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sensor", snapshotInput.sensorId()));

            if (sensor.getEquipment() == null ||
                    !sensor.getEquipment().getEquipmentId().equals(equipment.getEquipmentId())) {
                throw new DomainValidationException("Sensor does not belong to the same equipment as the Alert.");
            }

            snapshots.add(new AlertSensorSnapshot(sensor, snapshotInput.valueAtTrigger()));
        }

        Severity resolvedSeverity = resolveSeverity(input.severity(), ruleGroup.getSeverity());

        Alert alert = new Alert(
                equipment,
                ruleGroup,
                resolvedSeverity,
                input.message(),
                snapshots
        );

        return alertRepository.save(alert);
    }

    private Severity resolveSeverity(Severity inputSeverity, Severity ruleGroupSeverity) {
        if (inputSeverity != null && inputSeverity != Severity.REGULAR) {
            return inputSeverity;
        }
        if (ruleGroupSeverity != null && ruleGroupSeverity != Severity.REGULAR) {
            return ruleGroupSeverity;
        }
        return Severity.INFO;
    }
}
