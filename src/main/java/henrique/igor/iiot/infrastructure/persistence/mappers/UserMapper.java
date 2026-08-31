package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.infrastructure.persistence.entities.UserJpaEntity;

public class UserMapper {

    public static UserJpaEntity toJpaEntity(User domain){
        if (domain == null) return null;
        return new UserJpaEntity(
                domain.getUserId(),
                domain.getName(),
                domain.getEmail(),
                domain.getPassword(),
                domain.getPasswordRecovery(),
                domain.getUserRole(),
                domain.getSector() != null ? SectorMapper.toJpaEntity(domain.getSector()) : null,
                domain.getCreatedAt()
        );
    }

    public static User toDomain(UserJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new User(
                jpaEntity.getUserId(),
                jpaEntity.getName(),
                jpaEntity.getEmail(),
                jpaEntity.getPassword(),
                jpaEntity.getPasswordRecovery(),
                jpaEntity.getUserRole(),
                jpaEntity.getSector() != null ? SectorMapper.toDomain(jpaEntity.getSector()) : null,
                jpaEntity.getCreatedAt()
        );
    }
}
