package pizzaOrder.client.controller;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Controller
public class HomeController extends AbstractController{
	
	@Autowired
	RestaurantService restaurantService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	/**
	 * Homapage
	 * Get list of all restaurants in db an add it to Model
	 * @throws MessagingException 
	 */	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showAllRestaurants(Model model) throws MessagingException {

		List<Restaurant> restaurantList = restaurantService.getAllRestaurantsList();
		model.addAttribute("restaurants", restaurantList); 

		getActualUser(model);
		
		return "home";
	}
}

