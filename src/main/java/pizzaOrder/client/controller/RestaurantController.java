package pizzaOrder.client.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.type.TypeReference;

import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.client.service.MenuService;
import pizzaOrder.client.service.RestaurantService;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Controller
public class RestaurantController extends AbstractController{

	@Autowired
	RestaurantService restaurantService;
	
	@Autowired
	MenuService menuService;
	
	@RequestMapping(value = "/restaurant/{restaurantId}")
	public String showRestaurantById(@PathVariable("restaurantId") Long restaurantId, Model model){

		Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
		model.addAttribute("restaurant",restaurant);
		
		getActualUser(model);
		List<Menu> menuList = menuService.getMenuByRestaurantId(restaurantId);
		model.addAttribute("menu", menuList);

		return "restaurant";
	}

}
