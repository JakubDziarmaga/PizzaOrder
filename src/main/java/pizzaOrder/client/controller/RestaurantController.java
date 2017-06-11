package pizzaOrder.client.controller;

import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.client.service.interfaces.UserService;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.stars.Stars;

@Controller
public class RestaurantController extends AbstractController{

	@Autowired
	private RestaurantService restaurantService;
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private UserService userService;
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

		Stars stars = restaurantService.getStarsByRestaurantId(idRestaurant);
		model.addAttribute("stars", stars);

		return "restaurant";
	}
	
	@RequestMapping(value = "/restaurant/{idRestaurant}/image")
	@ResponseBody
	public byte[] helloWorld(@PathVariable("idRestaurant") Long idRestaurant)  {
		
		Restaurant restaurant= restaurantService.getRestaurantById(idRestaurant);
		
		return restaurant.getPhoto();
	}
	
	@RequestMapping(value = "/score/{idRestaurant}", method = RequestMethod.POST)
    public String registration(Integer rating, Model model,@PathVariable("idRestaurant") Long restaurantId) {
    	        
		restaurantService.addStar(restaurantId, rating);
		
        return "redirect:/restaurant/{idRestaurant}";
    }
}
