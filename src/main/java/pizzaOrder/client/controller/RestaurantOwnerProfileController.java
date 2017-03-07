package pizzaOrder.client.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import pizzaOrder.client.service.IndentService;
import pizzaOrder.client.service.MenuService;
import pizzaOrder.client.service.RestaurantService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
@SessionAttributes({"actualUser","ingredients","restaurant"})
public class RestaurantOwnerProfileController extends AbstractController{

	@Autowired
	RestaurantService restaurantService;
	
	@Autowired
	MenuService menuService;
	
	@Autowired
	IndentService indentService;
	
	@RequestMapping("/restaurantowner")
	public String findRestaurantByOwner(Model model) throws JsonParseException, JsonMappingException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	
		getActualUser(model);

		Long userId = template
				.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, auth.getName())
				.getId();
		
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

}
