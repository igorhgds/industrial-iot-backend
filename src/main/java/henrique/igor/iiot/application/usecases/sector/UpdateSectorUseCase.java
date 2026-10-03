package henrique.igor.iiot.application.usecases.sector;

import henrique.igor.iiot.application.usecases.sector.dto.UpdateSectorInput;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.SectorRepository;

public class UpdateSectorUseCase {

    private final SectorRepository sectorRepository;

    public UpdateSectorUseCase(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    public Sector execute(UpdateSectorInput input){
        Sector existingSector = sectorRepository.findById(input.sectorId())
                .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + input.sectorId()));

        if(!existingSector.getName().equalsIgnoreCase(input.name())) {
            sectorRepository.findByName(input.name()).ifPresent(s -> {
                throw new IllegalArgumentException("Sector with name " + input.name() + " already exists.");
            });
        }

        existingSector.updateInfo(input.name(), input.description());

        return sectorRepository.save(existingSector);
    }
}
