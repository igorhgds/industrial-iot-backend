package henrique.igor.iiot.application.usecases.gateway;

import henrique.igor.iiot.application.usecases.gateway.dto.RegisterGatewayInput;
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

    public Gateway execute(RegisterGatewayInput input) {
        Sector sector = null;
        if (input.sectorId() != null) {
            sector = sectorRepository.findById(input.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + input.sectorId()));
        }

        String gatewayCode = input.code();
        if (gatewayCode == null || gatewayCode.isBlank()) {
            gatewayCode = gatewayCodeGenerator.generate(sector);
        } else {
            if (gatewayRepository.findByCode(gatewayCode).isPresent()) {
                throw new IllegalArgumentException("Gateway with code " + gatewayCode + " already exists.");
            }
        }

        Gateway newGateway = new Gateway(
                gatewayCode,
                input.macAddress(),
                input.ipAddress(),
                input.firmwareVersion(),
                input.status(),
                sector,
                null
        );

        return gatewayRepository.save(newGateway);
    }
}
