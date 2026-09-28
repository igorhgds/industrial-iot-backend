package henrique.igor.iiot.application.usecases.gateway;

import henrique.igor.iiot.application.usecases.gateway.dto.UpdateGatewayRequest;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;

public class UpdateGatewayUseCase {

    private final GatewayRepository gatewayRepository;
    private final SectorRepository sectorRepository;

    public UpdateGatewayUseCase(GatewayRepository gatewayRepository, SectorRepository sectorRepository) {
        this.gatewayRepository = gatewayRepository;
        this.sectorRepository = sectorRepository;
    }

    public Gateway execute(UpdateGatewayRequest request){
        Gateway gateway = gatewayRepository.findById(request.gatewayId())
                .orElseThrow(() -> new IllegalArgumentException("Gateway not found with ID: " + request.gatewayId()));

        if (request.ipAddress() != null) {
            gateway.updateIpAddress(request.ipAddress());
        }

        if (request.firmwareVersion() != null) {
            gateway.updateFirmware(request.firmwareVersion());
        }

        if (request.status() != null) {
            gateway.changeStatus(request.status());
        }

        if (request.sectorId() != null) {
            Sector newSector = sectorRepository.findById(request.sectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + request.sectorId()));
            gateway.relocateToSector(newSector);
        }

        return gatewayRepository.save(gateway);
    }

}
