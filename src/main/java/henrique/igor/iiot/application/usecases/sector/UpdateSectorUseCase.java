package henrique.igor.iiot.application.usecases.sector;

import henrique.igor.iiot.application.usecases.sector.dto.UpdateSectorRequest;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.SectorRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateSectorUseCase {

    private final SectorRepository sectorRepository;

    public UpdateSectorUseCase(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    public Sector execute(UpdateSectorRequest request){
        Sector existingSector = sectorRepository.findById(request.sectorId())
                .orElseThrow(() -> new IllegalArgumentException("Sector not found with ID: " + request.sectorId()));

        if(!existingSector.getName().equalsIgnoreCase(request.name())) {
            sectorRepository.findByName(request.name()).ifPresent(s -> {
                throw new IllegalArgumentException("Sector with name " + request.name() + " already exists.");
            });
        }

        Sector updatedSector = new Sector(
                existingSector.getSectorId(),
                request.name(),
                request.description(),
                existingSector.getCreatedAt()
        );

        return sectorRepository.save(updatedSector);
    }
}
