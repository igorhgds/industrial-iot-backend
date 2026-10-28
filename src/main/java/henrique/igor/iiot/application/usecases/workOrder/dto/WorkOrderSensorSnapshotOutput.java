package henrique.igor.iiot.application.usecases.workOrder.dto;

import henrique.igor.iiot.domain.entities.enums.SensorType;

import java.math.BigDecimal;

public record WorkOrderSensorSnapshotOutput(
        String sensorCode,
        SensorType sensorType,
        String unit,
        BigDecimal valueAtTrigger
) {
}
