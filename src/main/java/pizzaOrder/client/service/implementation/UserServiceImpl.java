package pizzaOrder.client.service.implementation;

import java.net.URI;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void addNonActivatedUser(NonActivatedUser user) throws MessagingException {
		URI nonActivatedUserUri = defaultTemplate.postForLocation("http://localhost:8080/nonactivatedusers", user,NonActivatedUser.class);
		Long id = defaultTemplate.getForObject(nonActivatedUserUri, NonActivatedUser.class).getId();
		user.setId(id);
//		sendActivatingMail(user);	//TODO uncomment
	}

	@Override
	public void sendActivatingMail(NonActivatedUser user) throws MessagingException{
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom("pizza0rd3r@gmail.com");
		helper.setTo(user.getMail());
		helper.setSubject("PizzaOrder");
		helper.setText("Hello  " + user.getUsername() + ". Here's your activation link: http://localhost:8080/activate/"
				+ user.getId());

		mailSender.send(message);
	}

	@Override
	public void activateUser(Long nonActivatedUserId) {
//		RestTemplate template = new RestTemplate();
		User user = defaultTemplate.getForObject("http://localhost:8080/nonactivatedusers/{nonActivatedUserId}", User.class,nonActivatedUserId);
		user.setId(null);
		defaultTemplate.delete("http://localhost:8080/nonactivatedusers/{nonActivatedUserId}", nonActivatedUserId);
		userSecurityService.save(user);
		securityService.autologin(user.getUsername(), user.getPassword());
	}

	@Override 
	public Long getActualUserId(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		return halTemplate.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, auth.getName()).getId();
	}



}