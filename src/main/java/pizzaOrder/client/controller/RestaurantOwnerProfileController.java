package pizzaOrder.client.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.hateoas.PagedResources;
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

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
@SessionAttributes({"actualUser","ingredients","restaurant"})
public class RestaurantOwnerProfileController extends AbstractController{

	@RequestMapping("/restaurantowner")
	public String findRestaurantByOwner(Model model) throws JsonParseException, JsonMappingException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	
		getActualUser(model);

		Long userId = template
				.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, auth.getName())
				.getId();
		
		Restaurant restaurant;
		try{
		restaurant = template.getForObject("http://localhost:8080/restaurants/search/owner?ownerId={ownerId}", Restaurant.class, userId);
		}
		catch(HttpClientErrorException e){
			return "redirect:/addRestaurant";		//Redirect to :/addRestaurant when restaurantOwner doesn't have any restaurant 
		}
				
		model.addAttribute("restaurant", restaurant);
		
		getMenuByRestaurantId(model, restaurant);
		
		Long restaurantId = restaurant.getId();		
		getPayedIndentsByRestaurantId(model, restaurantId);
	
		return "restaurantOwner";
	}

	private void getMenuByRestaurantId(Model model, Restaurant restaurant) {
		String menuUrl = template.getForObject("http://localhost:8080/restaurants/{restaurantId}",PagedResources.class,restaurant.getId()).getLink("menu").getHref();
		
		Collection<?> menuHal = template.getForObject(menuUrl, PagedResources.class).getContent();				
		List<Menu> menu = mapper.convertValue(menuHal, new TypeReference<List<Menu>>() {});
	
		for(Menu m :menu){
			Collection<?> ingredientsHal = template.getForObject("http://localhost:8080/menu/{menuId}/ingredients", PagedResources.class, m.getId()).getContent();
			List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
			m.setIngredients(ingredients);
		}
		
		model.addAttribute("menu", menu);
	}

	//Add to model indents that are paid by user
	private void getPayedIndentsByRestaurantId(Model model, Long restaurantId) {
		Collection<?> indentsHal =   template.getForObject("http://localhost:8080/restaurants/{restaurantId}/indent",PagedResources.class,restaurantId).getContent();
		List<Indent> indents = mapper.convertValue(indentsHal, new TypeReference<List<Indent>>() {});
	
		List<Indent> payedIndents = new ArrayList<Indent>();
	
		for(Indent indent : indents){
			if(!indent.isPaid()) continue;
			
			User user = template.getForObject("http://localhost:8080/indents/{id}/user", User.class,indent.getId());
			indent.setUser(user);
			
			Menu tempMenu = template.getForObject("http://localhost:8080/indents/{id}/menu", Menu.class,indent.getId());
			
			Collection<?> ingredientsHal =template.getForObject("http://localhost:8080/menu/{id}/ingredients", PagedResources.class,tempMenu.getId()).getContent();
			List<Ingredients> tempIngredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients> >() {});
			
			tempMenu.setIngredients(tempIngredients);
			indent.setMenu(tempMenu);
			
			payedIndents.add(indent);
		}
		model.addAttribute("indents",payedIndents);
	}


	

}
