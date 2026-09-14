package henrique.igor.iiot.infrastructure.persistence.entities;

import henrique.igor.iiot.domain.entities.enums.GatewayStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "gateways")
@Getter
@Setter
public class GatewayJpaEntity {

    @Id
    private UUID gatewayId;

    //TODO must a code with type + anything
    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String macAddress;

    private String ipAddress;

    private String firmwareVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GatewayStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private SectorJpaEntity sector;

    private OffsetDateTime lastPing;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "gateway", fetch = FetchType.LAZY)
    private List<EquipmentJpaEntity> equipment;

    public GatewayJpaEntity(){}

    public GatewayJpaEntity(UUID gatewayId, String code, String macAddress, String ipAddress, String firmwareVersion, GatewayStatus status, SectorJpaEntity sector, OffsetDateTime lastPing, OffsetDateTime createdAt) {
        this.gatewayId = gatewayId;
        this.code = code;
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.firmwareVersion = firmwareVersion;
        this.status = status;
        this.sector = sector;
        this.lastPing = lastPing;
        this.createdAt = createdAt;
    }
}
