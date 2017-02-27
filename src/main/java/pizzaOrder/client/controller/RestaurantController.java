package pizzaOrder.client.controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
public class RestaurantController {
	
	@Autowired
	private RestTemplate template;

	@Autowired
	@Qualifier("configureHalObjectMapper")
	private ObjectMapper mapper;
	
	@RequestMapping(value = "/restaurant/{id}")
	public String showRestaurantById(@PathVariable("id") Long id, Model model) throws JsonParseException, JsonMappingException, IOException {	
	
		try{
		Restaurant restaurant = template.getForObject("http://localhost:8080/restaurants/{id}", Restaurant.class, id);
		model.addAttribute("restaurant", restaurant);
		}
		catch(HttpClientErrorException e){
			throw new RestaurantNotFoundException(id);
		}
		
		String menuUrl = template.getForObject("http://localhost:8080/restaurants/{id}", PagedResources.class, id).getLink("menu").getHref();

		List<Menu> menu = new ArrayList<Menu>(template.getForObject(menuUrl, PagedResources.class).getContent());
		model.addAttribute("menus", menu);

		Map<Long,List<Ingredients>> ingredientsByMenu = new HashMap<Long,List<Ingredients>>();
		
		List<Menu> pojos = mapper.convertValue(menu, new TypeReference<List<Menu>>() { });
		
		int i=0;
		for(Menu pojo:pojos) {
			String ingredientsUrl = mapper.convertValue(menu.get(i), PagedResources.class).getLink("ingredients").getHref();
			List<Ingredients> ingredients = new ArrayList<Ingredients>(template.getForObject(ingredientsUrl, PagedResources.class).getContent());
			ingredientsByMenu.put(pojo.getId(),ingredients );
			i++;
		}

		model.addAttribute("ingredients", ingredientsByMenu);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//if (auth.getPrincipal() != "anonymousUser") {
			org.springframework.security.core.userdetails.User actualUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
			model.addAttribute("actualUser", actualUser);
		//}

		return "restaurant";
	}

}
