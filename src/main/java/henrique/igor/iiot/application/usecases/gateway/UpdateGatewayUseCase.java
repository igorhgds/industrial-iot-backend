package henrique.igor.iiot.application.usecases.gateway;

import henrique.igor.iiot.application.usecases.gateway.dto.UpdateGatewayInput;
import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.domain.repositories.SectorRepository;

public class UpdateGatewayUseCase {

    private final GatewayRepository gatewayRepository;
    private final SectorRepository sectorRepository;

    public UpdateGatewayUseCase(GatewayRepository gatewayRepository, SectorRepository sectorRepository) {
        this.gatewayRepository = gatewayRepository;
        this.sectorRepository = sectorRepository;
    }

    public Gateway execute(UpdateGatewayInput input){
        Gateway gateway = gatewayRepository.findById(input.gatewayId())
                .orElseThrow(() -> new ResourceNotFoundException("Gateway", input.gatewayId()));

        if (input.ipAddress() != null) {
            gateway.updateIpAddress(input.ipAddress());
        }

        if (input.firmwareVersion() != null) {
            gateway.updateFirmware(input.firmwareVersion());
        }

        if (input.status() != null) {
            gateway.changeStatus(input.status());
        }

        if (input.sectorId() != null) {
            Sector newSector = sectorRepository.findById(input.sectorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sector", input.sectorId()));
            gateway.relocateToSector(newSector);
        }

        return gatewayRepository.save(gateway);
    }

}
