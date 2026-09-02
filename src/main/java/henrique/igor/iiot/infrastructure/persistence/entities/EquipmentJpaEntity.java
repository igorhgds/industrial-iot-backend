package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.EquipStatus;
import henrique.igor.iiot.domain.entities.enums.EquipType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "equipments")
@Getter
@Setter
public class EquipmentJpaEntity {

    @Id
    private UUID equipmentId;

    @Column(nullable = false, unique = true) //TODO must a code with type + anything
    private String equipCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EquipType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EquipStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private SectorJpaEntity sector;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public EquipmentJpaEntity(){}

    public EquipmentJpaEntity(UUID equipmentId, String equipCode, EquipType type, EquipStatus status, SectorJpaEntity sector, LocalDateTime createdAt) {
        this.equipmentId = equipmentId;
        this.equipCode = equipCode;
        this.type = type;
        this.status = status;
        this.sector = sector;
        this.createdAt = createdAt;
    }
}
