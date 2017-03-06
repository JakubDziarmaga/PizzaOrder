package pizzaOrder.security;

import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.users.User;
import pizzaOrder.restService.model.users.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
//    @Autowired
//    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole());
//        user.setRole(roleRepository.findOne(1L)); 
        RestTemplate template = new RestTemplate();
        template.postForObject("http://localhost:8080/users", user, User.class);
//        userRepository.save(user);					//TODO zrob to restowo
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}