package henrique.igor.iiot.application.usecases.sector.dto;

import java.util.UUID;

public record UpdateSectorRequest(
        UUID sectorId,
        String name,
        String description
) {
}
