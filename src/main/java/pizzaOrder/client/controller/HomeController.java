package pizzaOrder.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pizzaOrder.client.service.RestaurantService;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Controller
public class HomeController extends AbstractController{
	
	@Autowired
	RestaurantService restaurantService;
	
	@RequestMapping(value = {"/","home"}, method = RequestMethod.GET)
	public String showAllRestaurants(Model model) {

		List<Restaurant> restaurantList = restaurantService.getAllRestaurantsList();
		model.addAttribute("restaurants", restaurantList);

		getActualUser(model);
		
		return "home";
	}
}

