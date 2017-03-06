package pizzaOrder.client.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pizzaOrder.restService.model.restaurant.Restaurant;

@Controller
public class HomeController extends AbstractController{
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showAllRestaurants(Model model) {

		List<Restaurant> restaurants = new ArrayList<Restaurant>(template.getForObject("http://localhost:8080/restaurants", PagedResources.class).getContent());
		model.addAttribute("restaurants", restaurants);

		getActualUser(model);
		
		return "home";
	}
}

