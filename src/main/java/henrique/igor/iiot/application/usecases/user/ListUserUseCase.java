package henrique.igor.iiot.application.usecases.user;

import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

public class ListUserUseCase {

    private final UserRepository userRepository;

    public ListUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute(){
        return userRepository.findAll();
    }
}
