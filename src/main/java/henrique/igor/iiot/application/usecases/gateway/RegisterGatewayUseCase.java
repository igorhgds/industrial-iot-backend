package henrique.igor.iiot.application.usecases.gateway;

import henrique.igor.iiot.application.usecases.gateway.dto.RegisterGatewayRequest;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;

public class RegisterGatewayUseCase {

    private final GatewayRepository gatewayRepository;
    private final SectorRepository sectorRepository;

    public RegisterGatewayUseCase(GatewayRepository gatewayRepository, SectorRepository sectorRepository) {
        this.gatewayRepository = gatewayRepository;
        this.sectorRepository = sectorRepository;
    }

    public Gateway execute(RegisterGatewayRequest request){
        Sector sector = null;
        if (request.sectorId() != null) {
            sector = sectorRepository.findById(request.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + request.sectorId()));
        }

        String gatewayCode = request.code();
        if (gatewayCode == null || gatewayCode.isBlank()) {
            gatewayCode = generateGatewayCode(sector);
        } else {
            if (gatewayRepository.findByCode(gatewayCode).isPresent()) {
                throw new IllegalArgumentException("Gateway with code " + gatewayCode + " already exists.");
            }
        }

        Gateway newGateway = new Gateway(
                gatewayCode,
                request.macAddress(),
                request.ipAddress(),
                request.firmwareVersion(),
                request.status(),
                sector,
                null
        );

        return gatewayRepository.save(newGateway);
    }

    private String generateGatewayCode(Sector sector) {
        String prefix = "GEN";
        long count;

        if (sector != null && sector.getName() != null && !sector.getName().isBlank()) {
            String cleanName = sector.getName().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
            if (cleanName.length() >= 3) {
                prefix = cleanName.substring(0, 3);
            } else {
                prefix = cleanName;
            }
            count = gatewayRepository.countBySectorId(sector.getSectorId());
        } else {
            count = gatewayRepository.countBySectorIdIsNull();
        }

        String sequence = String.format("%03d", count + 1);
        return "GW-" + prefix + "-" + sequence;
    }
}
