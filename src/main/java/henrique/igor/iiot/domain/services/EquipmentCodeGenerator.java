package henrique.igor.iiot.domain.services;

import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;

public class EquipmentCodeGenerator {

    private final EquipmentRepository equipmentRepository;

    public EquipmentCodeGenerator(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public String generate(EquipType type, Sector sector) {
        String typePrefix = getEquipTypePrefix(type);
        String sectorPrefix = "GEN";
        long count;

        if (sector != null && sector.getName() != null && !sector.getName().isBlank()) {
            String cleanName = sector.getName().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            if (cleanName.length() >= 3) {
                sectorPrefix = cleanName.substring(0, 3);
            } else {
                sectorPrefix = cleanName;
            }
            count = equipmentRepository.countBySectorIdAndType(sector.getSectorId(), type);
        } else {
            count = equipmentRepository.countBySectorIdIsNullAndType(type);
        }

        String sequence = String.format("%03d", count + 1);
        return typePrefix + "-" + sectorPrefix + "-" + sequence;
    }

    private String getEquipTypePrefix(EquipType type) {
        return switch (type) {
            case MOTOR -> "MTR";
            case HVAC -> "HVAC";
            case LIGHTING -> "LGT";
            case CONVEYOR -> "CNV";
            case COMPRESSOR -> "CMP";
            default -> type.name().length() >= 3 ? type.name().substring(0, 3).toUpperCase() : type.name().toUpperCase();
        };
    }
}
