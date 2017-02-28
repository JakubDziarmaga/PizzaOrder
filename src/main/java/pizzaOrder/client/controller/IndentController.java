package pizzaOrder.client.controller;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.exceptionHandler.IndentAlreadyPaid;
import pizzaOrder.client.exceptionHandler.IndentNotFoundException;
import pizzaOrder.client.exceptionHandler.MenuNotFoundException;
import pizzaOrder.client.exceptionHandler.NotPermittedException;
import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
public class IndentController {

	@Autowired
	private RestTemplate template;
	
//	@Autowired
//	@Qualifier("configureHalObjectMapper")
//	private ObjectMapper mapper;
	
	@RequestMapping(value = "/indent/pay/{id}", method = RequestMethod.GET)			//TODO check if actualUser is owner of this indent
	public String payForIndent(@PathVariable("id") Long id) {

		checkIfActualUserIsOwnerOfIndent(id);
			
		try{
			Indent indent = template.getForObject("http://localhost:8080/indents/{id}", Indent.class, id);
				if(indent.isPaid()) throw new IndentAlreadyPaid(id);
			indent.setPaid(true);
			template.put("http://localhost:8080/indents/{id}", indent, id);
		}
		catch(HttpClientErrorException e){
			throw new IndentNotFoundException(id);
		}
		return "redirect:/user";
	}

	private void checkIfActualUserIsOwnerOfIndent(Long idIndent) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		
		String userUrl = template.getForObject("http://localhost:8080/indents/{id}", PagedResources.class, idIndent).getLink("user").getHref();
		if(!username.equals(template.getForObject(userUrl,User.class).getUsername()))
			throw new NotPermittedException();
	}
	
	@RequestMapping(value = "/addindents/{idRestaurant}/{idMenu}")
	public String addIndents(@PathVariable("idRestaurant") Long idRestaurant,@PathVariable("idMenu") Long idMenu) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		
		checkIfActualUserCanModifyEntity(idRestaurant, idMenu, username);
		checkIfRestaurantExists(idRestaurant);		
		checkIfMenuExists(idMenu);
		
		RestTemplate template = new RestTemplate();
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());
		
		
		//TODO Delete magicnumbers
		//TODO let only owner of indents delete indents
	
		
		Indent indent = new Indent();
		indent.setDate(new Date());
		URI newURI = template.postForLocation("http://localhost:8080/indents/", indent);
		
		HttpEntity<String> restaurantEntity = new HttpEntity<String>("http://localhost:8080/restaurants/"+idRestaurant, reqHeaders);		
		template.exchange(newURI+"/restaurant",  HttpMethod.PUT, restaurantEntity, String.class);

		Long userLink=template.getForObject("http://localhost:8080/users/search/names?username={username}",Restaurant.class, username).getId();
		HttpEntity<String> userEntity = new HttpEntity<String>("http://localhost:8080/users/"+userLink, reqHeaders);		
		template.exchange(newURI+"/user",  HttpMethod.PUT, userEntity, String.class);
		
		HttpEntity<String> menuEntity = new HttpEntity<String>("http://localhost:8080/menu/"+idMenu, reqHeaders);		
		template.exchange(newURI+"/menu",  HttpMethod.PUT, menuEntity, String.class);		
		
		return "redirect:/user";
	}

	private void checkIfActualUserCanModifyEntity(Long idRestaurant, Long idMenu,String actualUserUsername) {	
		String restaurantUrl = template.getForObject("http://localhost:8080/menu/{idMenu}",PagedResources.class,idMenu).getLink("restaurant").getHref();
		if(!idRestaurant.equals(template.getForObject(restaurantUrl, Restaurant.class).getId())) 
			throw new NotPermittedException();
		
		Long ownerId = template.getForObject("http://localhost:8080/restaurants/{idRestaurant}", Restaurant.class ,idRestaurant).getOwnerId();
		if(!actualUserUsername.equals(template.getForObject("http://localhost:8080/users/1/{ownerId}", User.class, ownerId).getUsername()))
			throw new NotPermittedException();
	}

	private void checkIfMenuExists(Long idMenu) {
		try{
//			template.getForObject("http://localhost:8080/restaurants/{idMenu}", Restaurant.class,idMenu);
			template.getForObject("http://localhost:8080/menu/{idMenu}", Menu.class,idMenu);

		}
		catch(HttpClientErrorException e){
			throw new MenuNotFoundException(idMenu);
		}
	}

	private void checkIfRestaurantExists(Long idRestaurant) {
		try{
		template.getForObject("http://localhost:8080/restaurants/{idRestaurant}", Restaurant.class,idRestaurant);
		}
		catch(HttpClientErrorException e){
			throw new RestaurantNotFoundException(idRestaurant);
		}
	}
	
	@RequestMapping(value = "indent/delete/{idIndent}")
	public String deleteIndent(@PathVariable("idIndent") Long idIndent){
		
		checkIfActualUserIsOwnerOfIndent(idIndent);								
		
		try{
			template.delete("http://localhost:8080/indents/{idIndent}",idIndent);
		}
		catch(HttpClientErrorException e){
			throw new IndentNotFoundException(idIndent);
		}
		return "redirect:/user";
	}

	
}
