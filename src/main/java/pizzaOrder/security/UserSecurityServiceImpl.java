package pizzaOrder.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.restService.model.users.User;

@Service
public class UserSecurityServiceImpl implements UserSecurityService {

	@Autowired
	@Qualifier("defaultTemplate")
	private RestTemplate template;
	
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Encode password by BCryptPasswordEncoder
     * Post new User 
     */
    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

//        template.postForObject("https://pizzaindent.herokuapp.com/users", user, User.class);
        template.postForObject("http://localhost:8080/users", user, User.class);
    }

    @Override
    public User findByUsername(String username) {
    	
//    	return template.getForObject("https://limitless-eyrie-45489.herokuapp.com/users/search/names?username={username}", User.class, username);       
    	return template.getForObject("https://pizzaindent.herokuapp.com/names?username={username}", User.class, username);
    }
}