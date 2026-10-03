package henrique.igor.iiot.application.usecases.equipment.dto;

import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;

import java.util.UUID;

public record RegisterEquipmentInput(
        String equipCode,
        EquipType type,
        UUID sectorId,
        UUID gatewayId
) {
}
