package henrique.igor.iiot.domain.services;

import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.GatewayRepository;

public class GatewayCodeGenerator {

    private final GatewayRepository gatewayRepository;

    public GatewayCodeGenerator(GatewayRepository gatewayRepository) {
        this.gatewayRepository = gatewayRepository;
    }

    public String generate(Sector sector) {
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
