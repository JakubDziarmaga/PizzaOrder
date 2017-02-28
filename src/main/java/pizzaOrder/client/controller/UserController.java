package pizzaOrder.client.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.restService.model.users.User;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.temporaryUsers.NonActivatedUser;
import pizzaOrder.security.SecurityService;
import pizzaOrder.security.UserService;

@Controller
//@SessionAttributes("user")
public class UserController {
	
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
    
    @RequestMapping(value = "/activate/{nonActivatedUserId}")
    public String activateUser(Model model, @PathVariable("nonActivatedUserId") Long nonActivatedUserId){

    	RestTemplate template = new RestTemplate();
    	NonActivatedUser user = template.getForObject("http://localhost:8080/nonactivatedusers/{nonActivatedUserId}", NonActivatedUser.class,nonActivatedUserId);
    	template.postForObject("http://localhost:8080/users", user, NonActivatedUser.class);
    	template.delete("http://localhost:8080/nonactivatedusers/{nonActivatedUserId}",nonActivatedUserId);
    	
    	securityService.autologin(user.getUsername(), user.getPassword());
        return "redirect:/";

    }


}