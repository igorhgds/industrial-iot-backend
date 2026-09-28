package henrique.igor.iiot.application.usecases.sector;

import henrique.igor.iiot.application.usecases.sector.dto.CreateSectorRequest;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.SectorRepository;

public class CreateSectorUseCase {

    private final SectorRepository sectorRepository;

    public CreateSectorUseCase(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    public Sector execute(CreateSectorRequest request){
        if (sectorRepository.findByName(request.name()).isPresent()){
            throw new IllegalArgumentException("Sector with name " + request.name() + " already exists.");
        }

        Sector newSector = new Sector(
                request.name(),
                request.description()
        );

        return sectorRepository.save(newSector);
    }
}
