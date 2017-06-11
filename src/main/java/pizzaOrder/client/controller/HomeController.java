package pizzaOrder.client.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.client.service.interfaces.UserService;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Controller
public class HomeController extends AbstractController{
	
	@Autowired
	private RestaurantService restaurantService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * Homepage
	 * Get list of all restaurants in db an add it to Model
	 * @throws MessagingException 
	 */	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showAllRestaurants(Model model) {

		List<Restaurant> restaurantList = restaurantService.getAllRestaurantsList();
		model.addAttribute("restaurants", restaurantList); 



		getActualUser(model);
		
		return "home";
	}
	
	/**
	 * Get list of restaurants with city=name
	 */
	@RequestMapping(value="/city",method=RequestMethod.GET)
	public String showRestaurantsByCityName(@RequestParam String cityName,Model model){
		
		List<Restaurant> restaurantList = restaurantService.getRestaurantsByCity(cityName);
		model.addAttribute("restaurants",restaurantList);

		getActualUser(model);
		
		return "home";
	}
}

