package pizzaOrder.client.controller;

import java.net.URI;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.client.exceptionHandler.IndentAlreadyPaid;
import pizzaOrder.client.exceptionHandler.IndentNotFoundException;
import pizzaOrder.client.exceptionHandler.MenuNotFoundException;
import pizzaOrder.client.exceptionHandler.NotPermittedException;
import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
public class IndentController {

	@Autowired
	private RestTemplate template;
	
	//Add indent to actual logged user
	@RequestMapping(value = "/addindents/{idRestaurant}/{idMenu}")
	public String addIndents(@PathVariable("idRestaurant") Long idRestaurant, @PathVariable("idMenu") Long idMenu) {
		
		checkIfRestaurantExists(idRestaurant);
		checkIfMenuExists(idMenu);
		checkIfMenuBelongsToRestaurant(idRestaurant, idMenu);

		RestTemplate template = new RestTemplate();
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());

		Indent indent = new Indent();
		indent.setDate(new Date());			
		URI newIndentURI = template.postForLocation("http://localhost:8080/indents/", indent);
		
		//Posting restaurant to indent
		HttpEntity<String> restaurantEntity = new HttpEntity<String>("http://localhost:8080/restaurants/" + idRestaurant, reqHeaders);
		template.exchange(newIndentURI + "/restaurant", HttpMethod.PUT, restaurantEntity, String.class);

		//Posting user to indent
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = template.getForObject("http://localhost:8080/users/search/names?username={username}",Restaurant.class, auth.getName()).getId();
		HttpEntity<String> userEntity = new HttpEntity<String>("http://localhost:8080/users/" + userId, reqHeaders);
		template.exchange(newIndentURI + "/user", HttpMethod.PUT, userEntity, String.class);

		//Posting menu to indent
		HttpEntity<String> menuEntity = new HttpEntity<String>("http://localhost:8080/menu/" + idMenu, reqHeaders);
		template.exchange(newIndentURI + "/menu", HttpMethod.PUT, menuEntity, String.class);

		return "redirect:/user";
	}
	
	//Check if restaurant with id = idRestaurant exists in DB
	private void checkIfRestaurantExists(Long idRestaurant) {
		try {
			template.getForObject("http://localhost:8080/restaurants/{idRestaurant}", Restaurant.class, idRestaurant);
		} catch (HttpClientErrorException e) {
			throw new RestaurantNotFoundException(idRestaurant);
		}
	}
	
	//Check if menu with id = idMenu exists in DB
	private void checkIfMenuExists(Long idMenu) {
		try {
			template.getForObject("http://localhost:8080/menu/{idMenu}", Menu.class, idMenu);
		} catch (HttpClientErrorException e) {
			throw new MenuNotFoundException(idMenu);
		}
	}
	
	private void checkIfMenuBelongsToRestaurant(Long idRestaurant, Long idMenu) {
		String restaurantUrl = template
				.getForObject("http://localhost:8080/menu/{idMenu}", PagedResources.class, idMenu).getLink("restaurant")
				.getHref();
		if (!idRestaurant.equals(template.getForObject(restaurantUrl, Restaurant.class).getId()))
			throw new NotPermittedException();
	}
		
	@RequestMapping(value = "/indent/pay/{id}", method = RequestMethod.GET) 
	public String payForIndent(@PathVariable("id") Long id) {
		checkIfIndentExists(id);
		checkIfActualUserIsOwnerOfIndent(id);
		Indent indent = template.getForObject("http://localhost:8080/indents/{id}", Indent.class, id);
		if (indent.isPaid())
			throw new IndentAlreadyPaid(id);
		indent.setPaid(true);
		template.put("http://localhost:8080/indents/{id}", indent, id);

		return "redirect:/user";
	}
	
	//Check if indent with id = idIndent exists in DB
	private void checkIfIndentExists(Long idIndent) {
		try {
			template.getForObject("http://localhost:8080/indents/{idIndent}", Indent.class, idIndent);
		} catch (HttpClientErrorException e) {
			throw new IndentNotFoundException(idIndent);
		}
	}
	
	private void checkIfActualUserIsOwnerOfIndent(Long idIndent) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		String userUrl = template.getForObject("http://localhost:8080/indents/{id}", PagedResources.class, idIndent)
				.getLink("user").getHref();
		if (!username.equals(template.getForObject(userUrl, User.class).getUsername()))
			throw new NotPermittedException();
	}

	@RequestMapping(value = "indent/delete/{idIndent}")
	public String deleteIndent(@PathVariable("idIndent") Long idIndent) {

		checkIfIndentExists(idIndent);
		checkIfActualUserIsOwnerOfIndent(idIndent);

		template.delete("http://localhost:8080/indents/{idIndent}", idIndent);
		return "redirect:/user";
	}

}
