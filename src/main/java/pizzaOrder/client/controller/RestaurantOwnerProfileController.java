package pizzaOrder.client.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.client.service.interfaces.IngredientService;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.client.service.interfaces.UserService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
@SessionAttributes({"actualUser","ingredients","restaurant"})
public class RestaurantOwnerProfileController extends AbstractController{

	@Autowired
	@Qualifier("halTemplate")
	private RestTemplate halTemplate;
	
	@Autowired
	private RestaurantService restaurantService;
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private IndentService indentService;
	
	@Autowired
	private IngredientService ingredientsSrvice;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/restaurantowner")
	public String findRestaurantByOwner(Model model) throws JsonParseException, JsonMappingException, IOException {
	
		getActualUser(model);
		
		//TODO add findUserIdByUsername in UserServiceImpl and delete this 3 lines
		Long userId = userService.getActualUserId();
		
		Restaurant restaurant;
		try{
		restaurant = restaurantService.getRestaurantByOwnerId(userId);
		model.addAttribute("restaurant", restaurant);
		}
		catch(HttpClientErrorException e){
			return "redirect:/addRestaurant";		//Redirect to :/addRestaurant when restaurantOwner doesn't have any restaurant 
		}
				
		List<Menu> menuList = menuService.getMenuByRestaurantId(restaurant.getId());
		model.addAttribute("menu", menuList);
	
		Long restaurantId = restaurant.getId();		
		List<Indent> indentList =indentService.getPayedIndentsByRestaurantId(restaurantId);
		model.addAttribute("indents",indentList);

		return "restaurantOwner";
	}

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
