package pizzaOrder.client.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;

import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
@SessionAttributes({ "actualUser", "ingredients" ,"restaurant" })
public class ModifyRestaurantController extends AbstractController{

	//Showing addRestaurant form
	@RequestMapping(value = "/addRestaurant", method = RequestMethod.GET)
	public String addRestaurant(Model model) {
		
		getActualUser(model);

		Restaurant restaurant = new Restaurant();
		model.addAttribute("restaurant", restaurant);

		return "addRestaurant";
	}

	@RequestMapping(value = "/addRestaurant", method = RequestMethod.POST)
	public String addRestaurant(@Valid Restaurant restaurant, BindingResult bindingResult, Model model) {
		        
		if (bindingResult.hasErrors()) {
			model.addAttribute("restaurant", restaurant);
			return "addRestaurant";
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		Long userId = template
				.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username)
				.getId();

		restaurant.setOwnerId(userId);
		template.postForObject("http://localhost:8080/restaurants", restaurant, Restaurant.class);

		return "redirect:/restaurantowner";
	}

	//Showing addMenu form
	@RequestMapping(value = "/restaurantowner/{idRestaurant}/addmenu", method = RequestMethod.GET) 
	public String addMenu(Model model, @PathVariable("idRestaurant") Long idRestaurant) {
		checkIfRestaurantExists(idRestaurant);
		getActualUser(model);

		Menu menu = new Menu();
		model.addAttribute("menu", menu);

		List<Ingredients> ingredientsHal = new ArrayList<Ingredients>(template.getForObject("http://localhost:8080/ingredients", PagedResources.class).getContent());
		List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
		model.addAttribute("ingredients", ingredients);

		return "addMenu";
	}

	@RequestMapping(value = "/restaurantowner/{idRestaurant}/addmenu", method = RequestMethod.POST)
	public String addMenu(@Valid Menu menu, BindingResult bindingResult, Model model,@PathVariable("idRestaurant") Long idRestaurant) throws URISyntaxException {

		if (bindingResult.hasErrors()) {
			return "addMenu";
		}
		checkIfRestaurantExists(idRestaurant);

		RestTemplate template = new RestTemplate();
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());

		URI newMenuURI = template.postForLocation("http://localhost:8080/menu/", menu.getPrice());

		HttpEntity<String> restaurantEntity = new HttpEntity<String>("http://localhost:8080/restaurants/" + idRestaurant, reqHeaders);
		template.exchange(newMenuURI +"/restaurant", HttpMethod.PUT, restaurantEntity, String.class);
	

		HttpEntity<String> ingredientsEntity;
		for (Ingredients i : menu.getIngredients()) {
			ingredientsEntity = new HttpEntity<String>("http://localhost:8080/ingredients/" + i.getId(), reqHeaders);
			template.exchange(newMenuURI + "/ingredients", HttpMethod.POST, ingredientsEntity, String.class);
		}

		return "redirect:/restaurantowner";
	}
	
	//Check if restaurant with id = idRestaurant exists in DB
	private void checkIfRestaurantExists(Long idRestaurant) {
		try {
			template.getForObject("http://localhost:8080/restaurants/{idRestaurant}", Restaurant.class, idRestaurant);
		} catch (HttpClientErrorException e) {
			throw new RestaurantNotFoundException(idRestaurant);
		}
	}
}
