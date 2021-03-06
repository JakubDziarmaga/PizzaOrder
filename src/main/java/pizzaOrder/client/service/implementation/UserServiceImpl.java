package pizzaOrder.client.service.implementation;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.service.interfaces.UserService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.restaurant.Restaurant;
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
	@Qualifier("halObjectMapper")
	private ObjectMapper mapper;
	
	@Autowired
	private UserSecurityService userSecurityService;

	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private JavaMailSender mailSender;

	/**
	 * Post new NonActivatedUser to db
	 * Get its id from db and put it in NonActivatedUser entity
	 * @throws MessagingException 
	 */
	@Override
	public void addNonActivatedUser(NonActivatedUser user) throws MessagingException{
		URI nonActivatedUserUri = userSecurityService.save(user); 
//		URI nonActivatedUserUri = defaultTemplate.postForLocation("https://pizzaindent.herokuapp.com/nonactivatedusers", user,NonActivatedUser.class);
		Long id = defaultTemplate.getForObject(nonActivatedUserUri, NonActivatedUser.class).getId();
		user.setId(id);
		sendActivatingMail(user); 
	}

	/**
	 * Send to NonActivatedUser mail with activating link
	 * Activation link -> "http://localhost:8080/activate/" + user.getId()
	 * @see pizzaOrder.client.mail.MailConfig.java
	 */
	@Override
	public void sendActivatingMail(NonActivatedUser nonActivatedUser) throws MessagingException{
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom("pizza0rd3r@gmail.com");
		helper.setTo(nonActivatedUser.getMail());
		helper.setSubject("PizzaOrder");
		helper.setText("Hello  " + nonActivatedUser.getUsername() + ". Here's your activation link: https://pizzaindent.herokuapp.com/activate/"
				+ nonActivatedUser.getId());
//		helper.setText("Hello  " + nonActivatedUser.getUsername() + ". Here's your activation link: http://localhost:8080/activate/"
//				+ nonActivatedUser.getId());
		mailSender.send(message);
	}


	/**
	 * Delete NonActivatedUser from NonActivatedUser table and post it in User table with different id
	 * Autologin to server 
	 */
	@Override
	public void activateUser(Long nonActivatedUserId) {
//		User user = defaultTemplate.getForObject("http://localhost:8080/nonactivatedusers/{nonActivatedUserId}", User.class,nonActivatedUserId);
		User user = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/nonactivatedusers/{nonActivatedUserId}", User.class,nonActivatedUserId);
		user.setId(null);																							//id in NonActivatedUser table and in User tables should't be the same
//		defaultTemplate.delete("http://localhost:8080/nonactivatedusers/{nonActivatedUserId}", nonActivatedUserId);
		defaultTemplate.delete("https://pizzaindent.herokuapp.com/nonactivatedusers/{nonActivatedUserId}", nonActivatedUserId);
//		userSecurityService.save(user);
//		defaultTemplate.postForLocation("http://localhost:8080/users", user,User.class);
		defaultTemplate.postForLocation("https://pizzaindent.herokuapp.com/users", user,User.class);

		securityService.autologin(user.getUsername(), user.getPassword());
	}
	
	/**
	 * Get actual user from SecurityContext
	 * @return actual user id 
	 */
	@Override 
	public Long getActualUserId(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getPrincipal());
		if(auth.getPrincipal().equals("[anonymousUser]")){
			return null;
		}
		if(auth.getPrincipal().equals("anonymousUser")){
			return null;
		}
//		return defaultTemplate.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, auth.getName()).getId();
		return defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/users/search/names?username={username}", User.class, auth.getName()).getId();

	}

	@Override
	public User getUserByUsername(String username) {
		
//		return defaultTemplate.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username);
		return defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/users/search/names?username={username}", User.class, username);

	}

	@Override
	public boolean changePassword(String oldPassword, String newPassword) {
		return false;
	}

	@Override
	public void changeMail(String newMail) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		User user = defaultTemplate.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, auth.getName());
		User user = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/users/search/names?username={username}", User.class, auth.getName());

		user.setMail(newMail);
		defaultTemplate.put("http://localhost:8080/users/{id}",user,user.getId());	
		defaultTemplate.put("https://pizzaindent.herokuapp.com/users/{id}",user,user.getId());	

	}

	@Override
	public Integer getAmountOfUnpayedIndents() {
		Long id = getActualUserId();
		if(id==null) return 0;
		
//		Collection<Indent> indentHal = halTemplate.getForObject("http://localhost:8080/users/{id}/indent", PagedResources.class,id).getContent();
		Collection<Indent> indentHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/users/{id}/indent", PagedResources.class,id).getContent();

		List<Indent> indentList = mapper.convertValue(indentHal, new TypeReference<List<Indent>>() {});
		
		int amount = 0;
		
		for(Indent indent:indentList){
			if(!indent.isPaid()) amount++;
			
		}
		return amount;
	}
	
}
