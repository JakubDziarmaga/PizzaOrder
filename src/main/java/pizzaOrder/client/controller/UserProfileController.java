package pizzaOrder.client.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Controller
public class UserProfileController {
	
	@Autowired
	private RestTemplate template;
	
	@Autowired
	@Qualifier("configureHalObjectMapper")
	private ObjectMapper mapper;
	
	@RequestMapping("/user")
	public String showUserProfile(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		org.springframework.security.core.userdetails.User actualUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
		model.addAttribute("actualUser", actualUser);
		String username = auth.getName();
		
		String indentUrl = template.getForObject("http://localhost:8080/users/search/names?username={username}",PagedResources.class, username).getLink("indent").getHref();
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
