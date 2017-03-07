package pizzaOrder.client.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import pizzaOrder.client.service.IngredientService;
import pizzaOrder.client.service.MenuService;
import pizzaOrder.client.service.RestaurantService;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
@SessionAttributes({ "actualUser", "ingredients" ,"restaurant" })
public class ModifyRestaurantController extends AbstractController{

	@Autowired
	RestaurantService restaurantService;
	
	@Autowired
	MenuService menuService;
	
	@Autowired
	IngredientService ingredientsSrvice;
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

		restaurantService.addRestaurant(restaurant);

		return "redirect:/restaurantowner";
	}

	//Showing addMenu form
	@RequestMapping(value = "/restaurantowner/{idRestaurant}/addmenu", method = RequestMethod.GET) 
	public String addMenu(Model model, @PathVariable("idRestaurant") Long idRestaurant) {
		restaurantService.checkIfRestaurantExists(idRestaurant);
		getActualUser(model);

		Menu menu = new Menu();
		model.addAttribute("menu", menu);


		List<Ingredients> ingredientsList = ingredientsSrvice.getAllIngredients();
		model.addAttribute("ingredients", ingredientsList);

		return "addMenu";
	}

	@RequestMapping(value = "/restaurantowner/{idRestaurant}/addmenu", method = RequestMethod.POST)
	public String addMenu(@Valid Menu menu, BindingResult bindingResult, Model model,@PathVariable("idRestaurant") Long idRestaurant) throws URISyntaxException {

		if (bindingResult.hasErrors()) {
			return "addMenu";
		}
		restaurantService.checkIfRestaurantExists(idRestaurant);

		menuService.addMenu(menu, idRestaurant);

		return "redirect:/restaurantowner";
	}
	


}
