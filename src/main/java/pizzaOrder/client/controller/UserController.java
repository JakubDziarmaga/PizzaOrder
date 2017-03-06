package pizzaOrder.client.controller;

import java.net.URI;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.client.validator.UserValidator;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.users.User;
import pizzaOrder.security.SecurityService;
import pizzaOrder.security.UserService;

@Controller
//@SessionAttributes("user")
public class UserController {//extends AbstractController{
	
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
	private JavaMailSender mailSender;
    
   
    
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
    
    	NonActivatedUser nonActivatedUser = new NonActivatedUser();        	  
        model.addAttribute("nonActivatedUser", nonActivatedUser);

        return "register";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@Valid NonActivatedUser nonActivatedUser, BindingResult bindingResult,Model model){
    	

    	UserValidator userValidator = new UserValidator();
        userValidator.validate(nonActivatedUser, bindingResult);

    	if(bindingResult.hasErrors()){
    		model.addAttribute("nonActivatedUser",nonActivatedUser);
    		return "register";
    	}
    	
    	 RestTemplate template = new RestTemplate();
    	 
    	 URI nonActivatedUserUri = template.postForLocation("http://localhost:8080/nonactivatedusers",nonActivatedUser,NonActivatedUser.class);
         Long id =template.getForObject(nonActivatedUserUri, NonActivatedUser.class).getId();
         nonActivatedUser.setId(id);
         
//    	sendSimpleActivatingMail(user);			//TODO uncomment 

        return "redirect:/";
    }
    
    public void sendSimpleActivatingMail(NonActivatedUser user) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom("pizza0rd3r@gmail.com");
		helper.setTo(user.getMail());
		helper.setSubject("PizzaOrder");
		helper.setText("Hello  "+ user.getUsername()+". Here's your activation link: http://localhost:8080/activate/" + user.getId()); 
		
		mailSender.send(message);
	}

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");
   
//        if (logout != null)
//            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }
    
    /**
     * Delete user data from temporaryuser table and post it in user table
     * Autologin
     * Redirect to home page 
     */
    @RequestMapping(value = "/activate/{nonActivatedUserId}")
    public String activateUser(Model model, @PathVariable("nonActivatedUserId") Long nonActivatedUserId){

    	RestTemplate template = new RestTemplate();
    	User user = template.getForObject("http://localhost:8080/nonactivatedusers/{nonActivatedUserId}", User.class,nonActivatedUserId);
    	user.setId(null);
    	template.delete("http://localhost:8080/nonactivatedusers/{nonActivatedUserId}",nonActivatedUserId);
    	userService.save(user);
    	securityService.autologin(user.getUsername(), user.getPassword());
        return "redirect:/";

    }


}