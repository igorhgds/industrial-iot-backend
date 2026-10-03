package henrique.igor.iiot.application.usecases.sector;

import henrique.igor.iiot.application.usecases.sector.dto.CreateSectorInput;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.SectorRepository;

public class CreateSectorUseCase {

    private final SectorRepository sectorRepository;

    public CreateSectorUseCase(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    public Sector execute(CreateSectorInput input){
        if (sectorRepository.findByName(input.name()).isPresent()){
            throw new IllegalArgumentException("Sector with name " + input.name() + " already exists.");
        }

        Sector newSector = new Sector(
                input.name(),
                input.description()
        );

        return sectorRepository.save(newSector);
    }
}
