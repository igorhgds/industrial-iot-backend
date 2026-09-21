package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.Gateway;

import java.util.*;

public interface GatewayRepository {

    Gateway save(Gateway gateway);

    Optional<Gateway> findById(UUID gatewayId);

    Optional<Gateway> findByCode(String code);

    List<Gateway> findAll();

    void deleteById(UUID gatewayId);
}
