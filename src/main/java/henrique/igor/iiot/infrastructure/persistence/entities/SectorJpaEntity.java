package henrique.igor.iiot.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sectors")
@Getter
@Setter
public class SectorJpaEntity {

    @Id
    private UUID sectorId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SectorJpaEntity(){}

    public SectorJpaEntity(UUID sectorId, String name, String description, LocalDateTime createdAt) {
        this.sectorId = sectorId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }
}
