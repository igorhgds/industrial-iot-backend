package henrique.igor.iiot.application.usecases.sector;

import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.domain.repositories.SectorRepository;

import java.util.List;

public class ListSectorUseCase {

    private final SectorRepository sectorRepository;

    public ListSectorUseCase(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    public List<Sector> execute(){
        return sectorRepository.findAll();
    }
}
