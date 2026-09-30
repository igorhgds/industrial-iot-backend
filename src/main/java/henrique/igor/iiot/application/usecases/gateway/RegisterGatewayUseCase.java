package henrique.igor.iiot.application.usecases.gateway;

import henrique.igor.iiot.application.usecases.gateway.dto.RegisterGatewayRequest;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import henrique.igor.iiot.domain.services.GatewayCodeGenerator;

public class RegisterGatewayUseCase {

    private final GatewayRepository gatewayRepository;
    private final SectorRepository sectorRepository;
    private final GatewayCodeGenerator gatewayCodeGenerator;

    public RegisterGatewayUseCase(GatewayRepository gatewayRepository,
                                 SectorRepository sectorRepository,
                                 GatewayCodeGenerator gatewayCodeGenerator) {
        this.gatewayRepository = gatewayRepository;
        this.sectorRepository = sectorRepository;
        this.gatewayCodeGenerator = gatewayCodeGenerator;
    }

    public Gateway execute(RegisterGatewayRequest request) {
        Sector sector = null;
        if (request.sectorId() != null) {
            sector = sectorRepository.findById(request.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + request.sectorId()));
        }

        String gatewayCode = request.code();
        if (gatewayCode == null || gatewayCode.isBlank()) {
            gatewayCode = gatewayCodeGenerator.generate(sector);
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
}
