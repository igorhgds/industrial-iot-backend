package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.HealthStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "equipment_state")
@Getter
@Setter
public class EquipmentStateJpaEntity {

    @Id
    private UUID equipmentId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "equipment_id")
    private EquipmentJpaEntity equipment;

    @Enumerated(EnumType.STRING)
    private HealthStatus status;

    @Column(columnDefinition = "jsonb")
    private String lastPayload;

    private LocalDateTime updatedAt;

    public EquipmentStateJpaEntity(){}

    public EquipmentStateJpaEntity(EquipmentJpaEntity equipment, HealthStatus status, String lastPayload, LocalDateTime updatedAt) {
        this.equipment = equipment;
        this.status = status;
        this.lastPayload = lastPayload;
        this.updatedAt = updatedAt;
    }
}
