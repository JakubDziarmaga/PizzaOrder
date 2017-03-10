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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
	private RestTemplate template;
	
	@Autowired
	@Qualifier("halObjectMapper")
	protected ObjectMapper mapper;
	
	@Autowired
	private RestaurantService restaurantService;
	
	@Autowired
	private MenuService menuService;
	
	@Override
	public void payForIndent(Long idIndent) {
		checkIfIndentExists(idIndent);
		checkIfActualUserIsOwnerOfIndent(idIndent);
		RestTemplate template = new RestTemplate();
		Indent indent = template.getForObject("http://localhost:8080/indents/{id}", Indent.class, idIndent);
		if (indent.isPaid())
			throw new IndentAlreadyPaid(idIndent);
		indent.setPaid(true);
		template.put("http://localhost:8080/indents/{id}", indent, idIndent);
	}

	// Check if indent with id = idIndent exists in DB
	@Override
	public void checkIfIndentExists(Long idIndent) {
		RestTemplate template = new RestTemplate();

		try {
			template.getForObject("http://localhost:8080/indents/{idIndent}", Indent.class, idIndent);
		} catch (HttpClientErrorException e) {
			throw new IndentNotFoundException(idIndent);
		}
	}
	@Override
	public void checkIfActualUserIsOwnerOfIndent(Long idIndent) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		String userUrl = template.getForObject("http://localhost:8080/indents/{id}", PagedResources.class, idIndent)
				.getLink("user").getHref();
		if (!username.equals(template.getForObject(userUrl, User.class).getUsername()))
			throw new NotPermittedException();
	}

	@Override
	public void deleteIndent(Long idIndent) {

		checkIfIndentExists(idIndent);
		checkIfActualUserIsOwnerOfIndent(idIndent);

		template.delete("http://localhost:8080/indents/{idIndent}", idIndent);
	}

	@Override
	public void addIndents(Long idRestaurant, Long idMenu) {
		
		restaurantService.checkIfRestaurantExists(idRestaurant);
		menuService.checkIfMenuExists(idMenu);
		menuService.checkIfMenuBelongsToRestaurant(idRestaurant, idMenu);
		
		RestTemplate template = new RestTemplate();
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());

		Indent indent = new Indent();
		indent.setDate(new Date());			
		URI newIndentURI = template.postForLocation("http://localhost:8080/indents/", indent);
		
		//Posting restaurant to indent
		HttpEntity<String> restaurantEntity = new HttpEntity<String>("http://localhost:8080/restaurants/" + idRestaurant, reqHeaders);
		template.exchange(newIndentURI + "/restaurant", HttpMethod.PUT, restaurantEntity, String.class);

		//Posting user to indent
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long userId = template.getForObject("http://localhost:8080/users/search/names?username={username}",Restaurant.class, auth.getName()).getId();
		HttpEntity<String> userEntity = new HttpEntity<String>("http://localhost:8080/users/" + userId, reqHeaders);
		template.exchange(newIndentURI + "/user", HttpMethod.PUT, userEntity, String.class);

		//Posting menu to indent
		HttpEntity<String> menuEntity = new HttpEntity<String>("http://localhost:8080/menu/" + idMenu, reqHeaders);
		template.exchange(newIndentURI + "/menu", HttpMethod.PUT, menuEntity, String.class);		
	}

	@Override
	public List<Indent> getPayedIndentsByRestaurantId(Long restaurantId) {
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
		return payedIndents;
	}

	@Override
	public List<Indent> getIndentsByUsername(String username) {
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
		return indents;
	}
}
