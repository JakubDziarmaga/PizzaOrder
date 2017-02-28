package pizzaOrder.client.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
@SessionAttributes({"actualUser","ingredients"})
public class RestaurantOwnerProfileController {

	@Autowired
	private RestTemplate template;

	@Autowired
	@Qualifier("configureHalObjectMapper")
	private ObjectMapper mapper;
	
	@RequestMapping("/restaurantowner")
	public String findRestaurantByOwner(Model model) throws JsonParseException, JsonMappingException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

			org.springframework.security.core.userdetails.User actualUser = (org.springframework.security.core.userdetails.User) auth
					.getPrincipal();
			model.addAttribute("actualUser", actualUser);	


		Long userId = template
				.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username)
				.getId();
		
		Restaurant restaurant;
		try{
		restaurant = template.getForObject("http://localhost:8080/restaurants/search/owner?ownerId={ownerId}", Restaurant.class, userId);
		}
		catch(HttpClientErrorException e){
			return "redirect:/addRestaurant";
		}
		
		model.addAttribute("restaurant", restaurant);
		
		String menuUrl = template.getForObject("http://localhost:8080/restaurants/search/owner?ownerId={ownerId}",PagedResources.class, userId).getLink("menu").getHref();

		Collection<?> menuHal = template.getForObject(menuUrl, PagedResources.class).getContent();
				
		List<Menu> menu = mapper.convertValue(menuHal, new TypeReference<List<Menu>>() {});
	
		for(Menu m :menu){
			Collection<?> ingredientsHal = template.getForObject("http://localhost:8080/menu/{menuId}/ingredients", PagedResources.class, m.getId()).getContent();
			List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
			m.setIngredients(ingredients);
		}
		
		model.addAttribute("menu", menu);
		

		
		Long restaurantId = restaurant.getId();
		
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

	
		return "restaurantOwner";
	}

	

}
