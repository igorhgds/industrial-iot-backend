package henrique.igor.iiot.application.usecases.equipment.dto;

import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;

import java.util.UUID;

public record RegisterEquipmentRequest(
        String equipCode,
        EquipType type,
        EquipStatus status,
        UUID sectorId,
        UUID gatewayId
) {
}
