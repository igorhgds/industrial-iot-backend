package henrique.igor.iiot.application.usecases.gateway;

import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;

import java.util.List;
import java.util.UUID;

public class ListGatewaysBySectorUseCase {

    private final GatewayRepository gatewayRepository;
    private final SectorRepository sectorRepository;

    public ListGatewaysBySectorUseCase(GatewayRepository gatewayRepository, SectorRepository sectorRepository) {
        this.gatewayRepository = gatewayRepository;
        this.sectorRepository = sectorRepository;
    }

    public List<Gateway> execute(UUID sectorId) {
        if (sectorId == null) {
            throw new IllegalArgumentException("Sector ID cannot be null.");
        }

        sectorRepository.findById(sectorId)
                .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + sectorId));

        return gatewayRepository.findBySectorId(sectorId);
    }
}
