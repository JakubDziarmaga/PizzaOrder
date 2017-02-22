package pizzaOrder.client.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.restService.model.users.User;
import pizzaOrder.restSercive.model.temporaryUsers.NonActivatedUser;
import pizzaOrder.restService.model.indent.Indent;
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

    	NonActivatedUser user = new NonActivatedUser();        	  
          model.addAttribute("user", user);

        return "register";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@ModelAttribute("userForm") NonActivatedUser userForm) throws MessagingException {//, BindingResult bindingResult//, Model model
       
        
    	sendSimpleActivatingMail(userForm);

        
        
        
//		userService.save(userForm);
        
        
        System.out.println(userForm.getRole());
        
        ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);

		RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
        template.postForObject("http://localhost:8080/nonactivatedusers",userForm,NonActivatedUser.class );
        
        
        

        //securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm()); BYLO TAK
        System.out.println(userForm.getUsername());
        System.out.println(userForm.getPassword());

//        securityService.autologin(userForm.getUsername(), userForm.getPassword());
        System.out.println("ddddd");
        return "redirect:/";
    }
    
    public void sendSimpleActivatingMail(NonActivatedUser user) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setFrom("pizza0rd3r@gmail.com");
		helper.setTo(user.getMail());
		helper.setSubject("TEST");
		helper.setText("Witaj "+ user.getUsername()); 
		
		
//		FileSystemResource tonyPicture = new FileSystemResource("");
//		helper.addAttachment("profilePicture.jpg", tonyPicture);
		
		System.out.println("wysylanie wiadomosci");
		mailSender.send(message);
		System.out.println("wyslano wiadomosc");
	}

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }


}