package pizzaOrder.security;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
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
    public URI save(NonActivatedUser user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

//        template.postForObject("https://pizzaindent.herokuapp.com/users", user, User.class);
        return template.postForLocation("http://localhost:8080/nonactivatedusers", user,NonActivatedUser.class);
    }


}