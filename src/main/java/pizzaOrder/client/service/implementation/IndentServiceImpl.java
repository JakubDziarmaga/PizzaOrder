package pizzaOrder.client.service.implementation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.exceptionHandler.IndentAlreadyPaid;
import pizzaOrder.client.exceptionHandler.IndentNotFoundException;
import pizzaOrder.client.exceptionHandler.NotPermittedException;
import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Service
public class IndentServiceImpl implements IndentService {

	@Autowired
	@Qualifier("halTemplate")
	private RestTemplate halTemplate;
	
	@Autowired
	@Qualifier("defaultTemplate")
	private RestTemplate defaultTemplate;
	
	@Autowired
	@Qualifier("halObjectMapper")
	private ObjectMapper mapper;
	
	@Autowired
	private RestaurantService restaurantService;
	
	@Autowired
	private MenuService menuService;
	
	/**
	 * Change indent.isPaid to true 
	 * @throw IndentAlreadyPaid if indent was already paid
	 */
	@Override
	public void payForIndent(Long idIndent) {
		checkIfIndentExists(idIndent);
		checkIfActualUserIsOwnerOfIndent(idIndent);
		Indent indent = defaultTemplate.getForObject("http://localhost:8080/indents/{id}", Indent.class, idIndent);
		
		if (indent.isPaid())
			throw new IndentAlreadyPaid(idIndent);
		
		indent.setPaid(true);
		defaultTemplate.put("http://localhost:8080/indents/{id}", indent, idIndent);
	}

	/**
	 * Check if indent with id = idIndent exists in DB
	 * @throw IndentNotFoundException if it doesn't
	 */
	@Override
	public void checkIfIndentExists(Long idIndent) {

		try {
			defaultTemplate.getForObject("http://localhost:8080/indents/{idIndent}", Indent.class, idIndent);
		} catch (HttpClientErrorException e) {
			throw new IndentNotFoundException(idIndent);
		}
	}
	
	/**
	 * Check if actual user has indent with id = idIndent in DB
	 * @throw IndentNotFoundException if he hasn't
	 */
	@Override
	public void checkIfActualUserIsOwnerOfIndent(Long idIndent) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		String userUrl = halTemplate.getForObject("http://localhost:8080/indents/{id}", PagedResources.class, idIndent)
				.getLink("user").getHref();
		if (!username.equals(halTemplate.getForObject(userUrl, User.class).getUsername()))
			throw new NotPermittedException();
	}
	
	/**
	 * Delete indent if it exists in db and if actual user has indent with id = idIndent in DB
	 */
	@Override
	public void deleteIndent(Long idIndent) {

		checkIfIndentExists(idIndent);
		checkIfActualUserIsOwnerOfIndent(idIndent);

		halTemplate.delete("http://localhost:8080/indents/{idIndent}", idIndent);
	}
	
	/**
	 * Post new indent
	 * Link it with actual user, menu and restaurant
	 */
	@Override
	public void addIndents(Long idRestaurant, Long idMenu) {
		
		restaurantService.getRestaurantById(idRestaurant);
		menuService.checkIfMenuExists(idMenu);
		menuService.checkIfMenuBelongsToRestaurant(idRestaurant, idMenu);
		
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());

		Indent indent = new Indent();
		indent.setDate(new Date());																					//add CURRENT data to entity
		URI newIndentURI = defaultTemplate.postForLocation("http://localhost:8080/indents/", indent);
		
		//Posting restaurant to indent
		HttpEntity<String> restaurantEntity = new HttpEntity<String>("http://localhost:8080/restaurants/" + idRestaurant, reqHeaders);
		defaultTemplate.exchange(newIndentURI + "/restaurant", HttpMethod.PUT, restaurantEntity, String.class);

		//Posting user to indent
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = defaultTemplate.getForObject("http://localhost:8080/users/search/names?username={username}",User.class, auth.getName()).getId();
		HttpEntity<String> userEntity = new HttpEntity<String>("http://localhost:8080/users/" + userId, reqHeaders);
		defaultTemplate.exchange(newIndentURI + "/user", HttpMethod.PUT, userEntity, String.class);

		//Posting menu to indent
		HttpEntity<String> menuEntity = new HttpEntity<String>("http://localhost:8080/menu/" + idMenu, reqHeaders);
		defaultTemplate.exchange(newIndentURI + "/menu", HttpMethod.PUT, menuEntity, String.class);		
	}
	
	/**
	 * @return List of restaurant which are paid
	 */
	@Override
	public List<Indent> getPayedIndentsByRestaurantId(Long restaurantId) {
		Collection<?> indentsHal =   halTemplate.getForObject("http://localhost:8080/restaurants/{restaurantId}/indent",PagedResources.class,restaurantId).getContent();
		List<Indent> indents = mapper.convertValue(indentsHal, new TypeReference<List<Indent>>() {});
	
		List<Indent> payedIndents = new ArrayList<Indent>();
	
		for(Indent indent : indents){
			if(!indent.isPaid()) continue;
			
			//Get user entity
			User user = halTemplate.getForObject("http://localhost:8080/indents/{id}/user", User.class,indent.getId());
			
			//Get menu entity
			Menu menu = halTemplate.getForObject("http://localhost:8080/indents/{id}/menu", Menu.class,indent.getId());

			//Get ingredients entity
			Collection<?> ingredientsHal =halTemplate.getForObject("http://localhost:8080/menu/{id}/ingredients", PagedResources.class,menu.getId()).getContent();
			List<Ingredients> tempIngredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients> >() {});
			
			//Link entities
			menu.setIngredients(tempIngredients);
			indent.setMenu(menu);
			indent.setUser(user);

			payedIndents.add(indent);
		}
		return payedIndents;
	}
	
	/**
	 * @return List of restaurant which user added to cart
	 */
	@Override
	public List<Indent> getIndentsByUsername(String username) {
		String indentUrl = halTemplate.getForObject("http://localhost:8080/users/search/names?username={username}",PagedResources.class, username).getLink("indent").getHref();
		Collection<?> indentsHal = halTemplate.getForObject(indentUrl, PagedResources.class).getContent();		
		List<Indent> indents = mapper.convertValue(indentsHal, new TypeReference<List<Indent>>() {});

		for(Indent indent : indents){
			//Get restaurant entity
			Restaurant restaurant = halTemplate.getForObject("http://localhost:8080/indents/{indentId}/restaurant", Restaurant.class,indent.getId());
			
			//Get menu entity
			Menu menu = halTemplate.getForObject("http://localhost:8080/indents/{indentId}/menu", Menu.class,indent.getId());
			
			//Get ingredients entity
			Collection<?> ingredientsHal = halTemplate.getForObject("http://localhost:8080/menu/{menuId}/ingredients", PagedResources.class,menu.getId()).getContent();			
			List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
			
			//Link entities
			menu.setIngredients(ingredients);			
			indent.setMenu(menu);
			indent.setRestaurant(restaurant);
		}
		return indents;
	}
}
