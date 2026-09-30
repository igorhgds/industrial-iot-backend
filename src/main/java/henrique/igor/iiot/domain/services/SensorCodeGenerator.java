package henrique.igor.iiot.domain.services;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.enums.SensorType;
import henrique.igor.iiot.domain.repositories.SensorRepository;

public class SensorCodeGenerator {

    private final SensorRepository sensorRepository;

    public SensorCodeGenerator(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public String generate(SensorType type, Equipment equipment) {
        String typePrefix = getSensorTypePrefix(type);
        String equipRef;
        long count;

        if (equipment != null && equipment.getEquipCode() != null && !equipment.getEquipCode().isBlank()) {
            equipRef = equipment.getEquipCode();
            count = sensorRepository.countByEquipmentIdAndSensorType(equipment.getEquipmentId(), type);
        } else {
            equipRef = "GEN";
            count = sensorRepository.countByEquipmentIdIsNullAndSensorType(type);
        }

        String sequence = String.format("%03d", count + 1);
        return typePrefix + "-" + sequence + "-" + equipRef;
    }

    private String getSensorTypePrefix(SensorType type) {
        return switch (type) {
            case CURRENT -> "CUR";
            case VOLTAGE -> "VOL";
            case TEMPERATURE -> "TEM";
            case VIBRATION -> "VIB";
            case PRESSURE -> "PRE";
            default -> type.name().length() >= 3 ? type.name().substring(0, 3).toUpperCase() : type.name().toUpperCase();
        };
    }
}
