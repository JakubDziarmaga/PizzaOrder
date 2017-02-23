package pizzaOrder.client.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import aj.org.objectweb.asm.TypeReference;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Controller
public class HomeController {
	@Autowired
	private JavaMailSender mailSender;
	// TODO usuń 1 po testach
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showAllRestaurants(Model model) {
//sendActivatingMail();

		System.out.println(new Date());
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);

		RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));

		
		List<Restaurant> restaurants = new ArrayList<Restaurant>(template.getForObject("http://localhost:8080/restaurants", PagedResources.class).getContent());
		model.addAttribute("restaurants", restaurants);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();


		if (auth.getPrincipal() != "anonymousUser") {
			User actualUser = (User) auth.getPrincipal();
			model.addAttribute("actualUser", actualUser);
			System.out.println(actualUser.getUsername());
		}
		
		return "home";
	}
//	private void sendActivatingMail(String userMail,String Us) {
//		SimpleMailMessage message = new SimpleMailMessage();
//		    	
//		    	message.setFrom("pizza0rd3r@gmail.com");
//		    	message.setTo("qqob3ftm.v2j@20mail.eu");
//		    	message.setSubject("TEST");
//		    	message.setText("testowa wiadomosc"); 
//				
//				
//		//		FileSystemResource tonyPicture = new FileSystemResource("DS:/MVCC/aaaa/src/main/webapp/upload/tony.jpg");
//		//		helper.addAttachment("profilePicture.jpg", tonyPicture);
//				
//				System.out.println("wysylanie wiadomosci");
//				mailSender.send(message);
//				System.out.println("wyslano wiadomosc");
//	}
	
	
}
