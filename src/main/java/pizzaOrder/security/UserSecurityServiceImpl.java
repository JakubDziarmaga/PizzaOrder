package pizzaOrder.security;

import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.users.User;
import pizzaOrder.restService.model.users.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;

@Service
public class UserSecurityServiceImpl implements UserSecurityService {

	@Autowired
	@Qualifier("defaultTemplate")
	private RestTemplate template;
	
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole());

        template.postForObject("http://localhost:8080/users", user, User.class);
    }

    @Override
    public User findByUsername(String username) {
    	return template.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username);       

    }
}