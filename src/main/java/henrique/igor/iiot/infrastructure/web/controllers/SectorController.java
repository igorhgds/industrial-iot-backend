package henrique.igor.iiot.infrastructure.web.controllers;

import henrique.igor.iiot.application.usecases.sector.CreateSectorUseCase;
import henrique.igor.iiot.application.usecases.sector.ListSectorUseCase;
import henrique.igor.iiot.application.usecases.sector.UpdateSectorUseCase;
import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.infrastructure.web.dtos.sector.CreateSectorRequest;
import henrique.igor.iiot.infrastructure.web.dtos.sector.SectorResponse;
import henrique.igor.iiot.infrastructure.web.dtos.sector.UpdateSectorRequest;
import henrique.igor.iiot.infrastructure.web.specs.SectorControllerSpec;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sectors")
public class SectorController implements SectorControllerSpec {

    private final CreateSectorUseCase createSectorUseCase;
    private final ListSectorUseCase listSectorUseCase;
    private final UpdateSectorUseCase updateSectorUseCase;

    public SectorController(
            CreateSectorUseCase createSectorUseCase,
            ListSectorUseCase listSectorUseCase,
            UpdateSectorUseCase updateSectorUseCase
    ) {
        this.createSectorUseCase = createSectorUseCase;
        this.listSectorUseCase = listSectorUseCase;
        this.updateSectorUseCase = updateSectorUseCase;
    }

    @Override
    @PostMapping
    public ResponseEntity<SectorResponse> create(@Valid @RequestBody CreateSectorRequest request) {
        Sector createdSector = createSectorUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(SectorResponse.fromDomain(createdSector));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<SectorResponse>> listAll() {
        List<SectorResponse> sectors = listSectorUseCase.execute().stream()
                .map(SectorResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(sectors);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<SectorResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSectorRequest request
    ) {
        Sector updatedSector = updateSectorUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(SectorResponse.fromDomain(updatedSector));
    }
}
