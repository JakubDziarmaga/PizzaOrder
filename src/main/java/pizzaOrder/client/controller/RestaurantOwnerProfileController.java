package pizzaOrder.client.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.client.service.interfaces.IngredientService;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.client.service.interfaces.UserService;
import pizzaOrder.client.validator.MenuValidator;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
@SessionAttributes({"actualUser","ingredients"})//,"restaurant"})
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
	private IngredientService ingredientsService;
	
	@Autowired
	private UserService userService;
	
	@Autowired 
	private MenuValidator menuValidator;
	
    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
    		throws ServletException {

    		// Convert multipart object to byte[]
    		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());

    }
	
	/**
	 * Show restaurantOwnerPage
	 * Redirect to /addRestaurant page when actual user doesn't already have restaurant
	 */
	@RequestMapping("/restaurantOwner")
	public String findRestaurantByOwner(Model model) throws JsonParseException, JsonMappingException, IOException {
	
		getActualUser(model);
		
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
	
		List<Indent> indentList =indentService.getPayedIndentsByRestaurantId(restaurant.getId());
		model.addAttribute("indents",indentList);
		return "restaurantOwner";
	}

	/**
	 * Show addRestaurant form
	 */
	@RequestMapping(value = "/addRestaurant", method = RequestMethod.GET)
	public String addRestaurant(Model model) {
		
		getActualUser(model);

		Restaurant restaurant = new Restaurant();
		model.addAttribute("restaurant", restaurant);

		return "addRestaurant";
	}

	/**
	 * Add restaurant
	 * @return addRestaurant view if post entity isn't valid
	 * @return "redirect:/restaurantOwner" if entity was valid
	 */
	@RequestMapping(value = "/addRestaurant", method = RequestMethod.POST)
	public String addRestaurant(@Valid Restaurant restaurant, BindingResult bindingResult, Model model) {
		        
		if (bindingResult.hasErrors()) {
			model.addAttribute("restaurant", restaurant);
			return "addRestaurant";
		}

		restaurantService.addRestaurant(restaurant);

		return "redirect:/restaurantOwner";
	}

	/**
	 * Show addMenu form
	 */
	@RequestMapping(value = "/restaurantOwner/{idRestaurant}/addmenu", method = RequestMethod.GET) 
	public String addMenu(Model model, @PathVariable("idRestaurant") Long idRestaurant) {
		
		restaurantService.getRestaurantById(idRestaurant);				//Check if restaurant with idRestaurant exists in db
		
		getActualUser(model);

		model.addAttribute("menu", new Menu());

		List<Ingredients> ingredientsList = ingredientsService.getAllIngredients();
		model.addAttribute("ingredients", ingredientsList);

		return "addMenu";
	}

	/**
	 * Add menu to restaurant with id = idRestaurant
	 * @return addMenu view if posted entity isn't valid
	 * @return "redirect:/restaurantOwner" if entity was valid
	 */
	@RequestMapping(value = "/restaurantOwner/{idRestaurant}/addmenu", method = RequestMethod.POST)
	public String addMenu(@Valid Menu menu, BindingResult bindingResult, Model model,@PathVariable("idRestaurant") Long idRestaurant) throws URISyntaxException {

		menuValidator.validate(menu, bindingResult);
		if (bindingResult.hasErrors()) {
			return "addMenu";
		}
		
		restaurantService.getRestaurantById(idRestaurant);				//Check if restaurant with idRestaurant exists in db

		menuService.addMenu(menu, idRestaurant);

		return "redirect:/restaurantOwner";
	}
	

}
