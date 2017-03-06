package pizzaOrder.client.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.hateoas.PagedResources;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Controller
public class UserProfileController extends AbstractController{
	
	/**
	 * Show user's data with list of indents
	 */	
	@RequestMapping("/user")
	public String showUserProfile(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		getActualUser(model);
		
		String indentUrl = template.getForObject("http://localhost:8080/users/search/names?username={username}",PagedResources.class, auth.getName()).getLink("indent").getHref();
		Collection<?> indentsHal = template.getForObject(indentUrl, PagedResources.class).getContent();		
		List<Indent> indents = mapper.convertValue(indentsHal, new TypeReference<List<Indent>>() {});

		for(Indent indent : indents){
			indent.setRestaurant(template.getForObject("http://localhost:8080/indents/{indentId}/restaurant", Restaurant.class,indent.getId()));
			
			Menu menu = template.getForObject("http://localhost:8080/indents/{indentId}/menu", Menu.class,indent.getId());
			
			Collection<?> ingredientsHal = template.getForObject("http://localhost:8080/menu/{menuId}/ingredients", PagedResources.class,menu.getId()).getContent();
			
			List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
			
			menu.setIngredients(ingredients);			
			indent.setMenu(menu);
		}
		model.addAttribute("indents", indents);

		return "user";
	}
}
