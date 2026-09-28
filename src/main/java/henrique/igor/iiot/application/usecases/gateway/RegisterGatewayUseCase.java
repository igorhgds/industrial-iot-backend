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
        if(gatewayRepository.findByCode(request.code()).isPresent()){
            throw new IllegalArgumentException("Gateway with code " + request.code() + " already exists.");
        }
        Sector sector = null;
        if (request.sectorId() != null) {
            sector = sectorRepository.findById(request.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + request.sectorId()));
        }

        Gateway newGateway = new Gateway(
                request.code(),
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
