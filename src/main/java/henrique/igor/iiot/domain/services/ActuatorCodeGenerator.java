package henrique.igor.iiot.domain.services;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.enums.ActuatorType;
import henrique.igor.iiot.domain.repositories.ActuatorRepository;

public class ActuatorCodeGenerator {

    private final ActuatorRepository actuatorRepository;

    public ActuatorCodeGenerator(ActuatorRepository actuatorRepository) {
        this.actuatorRepository = actuatorRepository;
    }

    public String generate(ActuatorType type, Equipment equipment) {
        if (type == null) {
            throw new IllegalArgumentException("ActuatorType cannot be null.");
        }

        String typePrefix = getActuatorTypePrefix(type);
        String equipRef;
        long nextSequence;

        if (equipment != null && equipment.getEquipCode() != null && !equipment.getEquipCode().isBlank()) {
            equipRef = equipment.getEquipCode().trim();
            nextSequence = actuatorRepository.findMaxSequenceByEquipmentIdAndType(equipment.getEquipmentId(), type)
                    .orElse(0L) + 1;
        } else {
            equipRef = "GEN";
            nextSequence = actuatorRepository.findMaxSequenceForUnassignedEquipment(type)
                    .orElse(0L) + 1;
        }

        String sequence = String.format("%03d", nextSequence);
        return typePrefix + "-" + sequence + "-" + equipRef;
    }

    private String getActuatorTypePrefix(ActuatorType type) {
        return switch (type) {
            case MOTOR -> "MOT";
            case RELAY -> "REL";
            case VALVE -> "VAL";
            case HEATER -> "HEA";
            default -> type.name().length() >= 3 ? type.name().substring(0, 3).toUpperCase() : type.name().toUpperCase();
        };
    }
}