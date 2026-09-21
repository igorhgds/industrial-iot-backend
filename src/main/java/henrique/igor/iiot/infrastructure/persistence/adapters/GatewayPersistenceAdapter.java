package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.Gateway;
import henrique.igor.iiot.domain.repositories.GatewayRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.GatewayJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.GatewayMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.GatewayJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class GatewayPersistenceAdapter implements GatewayRepository {

    private final GatewayJpaRepository gatewayJpaRepository;

    public GatewayPersistenceAdapter(GatewayJpaRepository gatewayJpaRepository) {
        this.gatewayJpaRepository = gatewayJpaRepository;
    }

    @Override
    public Gateway save(Gateway gateway) {
        GatewayJpaEntity jpaEntity = GatewayMapper.toJpaEntity(gateway);
        GatewayJpaEntity savedEntity = gatewayJpaRepository.save(jpaEntity);
        return GatewayMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Gateway> findById(UUID gatewayId) {
        return gatewayJpaRepository.findById(gatewayId)
                .map(GatewayMapper::toDomain);
    }

    @Override
    public Optional<Gateway> findByCode(String code) {
        return gatewayJpaRepository.findByCode(code)
                .map(GatewayMapper::toDomain);
    }

    @Override
    public List<Gateway> findAll() {
        return gatewayJpaRepository.findAll()
                .stream()
                .map(GatewayMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID gatewayId) {
        gatewayJpaRepository.deleteById(gatewayId);
    }
}
