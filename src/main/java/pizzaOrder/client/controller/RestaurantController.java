package pizzaOrder.client.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Controller
public class RestaurantController extends AbstractController{

	@Autowired
	private RestaurantService restaurantService;
	
	@Autowired
	private MenuService menuService;
	
	/**
	 * Show restaurant data with id = idRestaurant
	 * Show list of menu belong to restaurant
	 */
	@RequestMapping(value = "/restaurant/{idRestaurant}")
	public String showRestaurantById(@PathVariable("idRestaurant") Long idRestaurant, Model model){

		Restaurant restaurant = restaurantService.getRestaurantById(idRestaurant);
		model.addAttribute("restaurant",restaurant);
		
		getActualUser(model);
		
		List<Menu> menuList = menuService.getMenuByRestaurantId(idRestaurant);
		model.addAttribute("menu", menuList);

		return "restaurant";
	}
	
	@RequestMapping(value = "/restaurant/{idRestaurant}/image")
	@ResponseBody
	public byte[] helloWorld(@PathVariable("idRestaurant") Long idRestaurant)  {
		
		Restaurant restaurant= restaurantService.getRestaurantById(idRestaurant);
		
		return restaurant.getPhoto();
	}

}
