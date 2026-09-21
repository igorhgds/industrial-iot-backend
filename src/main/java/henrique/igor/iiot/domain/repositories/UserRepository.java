package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.User;

import java.util.*;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID userId);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    void deleteById(UUID userId);
}
