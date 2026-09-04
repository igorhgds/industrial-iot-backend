package henrique.igor.iiot.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Sector {

    private final UUID sectorId;
    private final String name;
    private String description;
    private final LocalDateTime createdAt;

    public Sector(String name, String description) {
        this(UUID.randomUUID(), name, description, LocalDateTime.now());
    }

    public Sector(UUID sectorId, String name, String description, LocalDateTime createdAt) {
        this.sectorId = sectorId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public UUID getSectorId() {return sectorId;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
