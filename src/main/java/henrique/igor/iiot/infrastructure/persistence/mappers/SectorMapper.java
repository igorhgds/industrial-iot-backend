package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.Sector;
import henrique.igor.iiot.infrastructure.persistence.entities.SectorJpaEntity;

public class SectorMapper {

    public static SectorJpaEntity toJpaEntity(Sector domain){
        if (domain == null) return null;
        return new SectorJpaEntity(
                domain.getSectorId(),
                domain.getName(),
                domain.getDescription(),
                domain.getCreatedAt()
        );
    }

    public static Sector toDomain(SectorJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new Sector(
                jpaEntity.getSectorId(),
                jpaEntity.getName(),
                jpaEntity.getDescription(),
                jpaEntity.getCreatedAt()
        );
    }
}
