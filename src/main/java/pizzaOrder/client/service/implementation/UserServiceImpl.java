package pizzaOrder.client.service.implementation;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.client.service.interfaces.UserService;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.users.User;
import pizzaOrder.security.SecurityService;
import pizzaOrder.security.UserSecurityService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	@Qualifier("defaultTemplate")
	private RestTemplate defaultTemplate;
	
	@Autowired
	@Qualifier("halTemplate")
	private RestTemplate halTemplate;
	
	@Autowired
	private UserSecurityService userSecurityService;

	@Autowired
	private SecurityService securityService;

	/**
	 * Post new NonActivatedUser to db
	 * Get its id from db and put it in NonActivatedUser entity
	 */
	@Override
	public void addNonActivatedUser(NonActivatedUser user){
		URI nonActivatedUserUri = defaultTemplate.postForLocation("http://localhost:8080/nonactivatedusers", user,NonActivatedUser.class);
		Long id = defaultTemplate.getForObject(nonActivatedUserUri, NonActivatedUser.class).getId();
		user.setId(id);
	}


	/**
	 * Delete NonActivatedUser from NonActivatedUser table and post it in User table with different id
	 * Autologin to server 
	 */
	@Override
	public void activateUser(Long nonActivatedUserId) {
		User user = defaultTemplate.getForObject("http://localhost:8080/nonactivatedusers/{nonActivatedUserId}", User.class,nonActivatedUserId);
		user.setId(null);																							//id in NonActivatedUser table and in User tables should't be the same
		defaultTemplate.delete("http://localhost:8080/nonactivatedusers/{nonActivatedUserId}", nonActivatedUserId);
		userSecurityService.save(user);
		securityService.autologin(user.getUsername(), user.getPassword());
	}
	
	/**
	 * Get actual user from SecurityContext
	 * @return actual user id 
	 */
	@Override 
	public Long getActualUserId(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		return defaultTemplate.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, auth.getName()).getId();
	}

	@Override
	public User getUserByUsername(String username) {
		
		return defaultTemplate.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username);
	}



}
