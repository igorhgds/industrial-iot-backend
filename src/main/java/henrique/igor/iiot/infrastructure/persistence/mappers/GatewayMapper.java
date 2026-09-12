package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.infrastructure.persistence.entities.GatewayJpaEntity;

public class GatewayMapper {

    public static GatewayJpaEntity toJpaEntity(Gateway domain){
        if (domain == null) return null;
        return new GatewayJpaEntity(
                domain.getGatewayId(),
                domain.getCode(),
                domain.getMacAddress(),
                domain.getIpAddress(),
                domain.getFirmwareVersion(),
                domain.getStatus(),
                domain.getSector() != null ? SectorMapper.toJpaEntity(domain.getSector()) : null,
                domain.getLastPing(),
                domain.getCreatedAt()
        );
    }

    public static Gateway toDomain(GatewayJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new Gateway(
                jpaEntity.getGatewayId(),
                jpaEntity.getCode(),
                jpaEntity.getMacAddress(),
                jpaEntity.getIpAddress(),
                jpaEntity.getFirmwareVersion(),
                jpaEntity.getStatus(),
                jpaEntity.getSector() != null ? SectorMapper.toDomain(jpaEntity.getSector()) : null,
                jpaEntity.getLastPing(),
                jpaEntity.getCreatedAt()
        );
    }
}
