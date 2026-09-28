package henrique.igor.iiot.application.usecases.equipment;

import henrique.igor.iiot.application.usecases.equipment.dto.RegisterEquipmentRequest;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import henrique.igor.iiot.domain.repositories.EquipmentRepository;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;

public class RegisterEquipmentUseCase {

    private final EquipmentRepository equipmentRepository;
    private final SectorRepository sectorRepository;
    private final GatewayRepository gatewayRepository;

    public RegisterEquipmentUseCase(EquipmentRepository equipmentRepository, SectorRepository sectorRepository, GatewayRepository gatewayRepository) {
        this.equipmentRepository = equipmentRepository;
        this.sectorRepository = sectorRepository;
        this.gatewayRepository = gatewayRepository;
    }

    public Equipment execute(RegisterEquipmentRequest request) {
        if (request.type() == null) {
            throw new IllegalArgumentException("Equipment type cannot be null.");
        }

        Sector sector = null;
        if (request.sectorId() != null) {
            sector = sectorRepository.findById(request.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + request.sectorId()));
        }

        Gateway gateway = null;
        if (request.gatewayId() != null) {
            gateway = gatewayRepository.findById(request.gatewayId())
                    .orElseThrow(() -> new IllegalArgumentException("Gateway not found with ID: " + request.gatewayId()));
        }

        String equipCode = request.equipCode();
        if (equipCode == null || equipCode.isBlank()) {
            equipCode = generateEquipmentCode(request.type(), sector);
        } else {
            if (equipmentRepository.findByEquipCode(equipCode).isPresent()) {
                throw new IllegalArgumentException("Equipment with code " + equipCode + " already exists.");
            }
        }

        EquipStatus status = request.status() != null ? request.status() : EquipStatus.ACTIVE;

        Equipment newEquipment = new Equipment(
                equipCode,
                request.type(),
                sector,
                gateway
        );
        newEquipment.changeStatus(status);

        return equipmentRepository.save(newEquipment);
    }

    private String generateEquipmentCode(EquipType type, Sector sector) {
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
