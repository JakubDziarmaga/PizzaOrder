package pizzaOrder.client.service.implementation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.exceptionHandler.MenuNotFoundException;
import pizzaOrder.client.exceptionHandler.NotPermittedException;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Service
public class MenuServiceImpl implements MenuService {
	
	@Autowired
	@Qualifier("halTemplate")
	private RestTemplate halTemplate;
	
	@Autowired
	@Qualifier("defaultTemplate")
	private RestTemplate defaultemplate;
	
	@Autowired
	@Qualifier("halObjectMapper")
	private ObjectMapper mapper;
	

	@Override
	public void checkIfMenuExists(Long idMenu) {
		try {
//			RestTemplate template = new RestTemplate();
			defaultemplate.getForObject("http://localhost:8080/menu/{idMenu}", Menu.class, idMenu);
		} catch (HttpClientErrorException e) {
			throw new MenuNotFoundException(idMenu);
		}
	}

	@Override
	public void checkIfMenuBelongsToRestaurant(Long idRestaurant, Long idMenu) {
		String restaurantUrl = halTemplate
				.getForObject("http://localhost:8080/menu/{idMenu}", PagedResources.class, idMenu).getLink("restaurant")
				.getHref();
		if (!idRestaurant.equals(halTemplate.getForObject(restaurantUrl, Restaurant.class).getId()))
			throw new NotPermittedException();
	}

	@Override
	public void addMenu(Menu menu, Long idRestaurant)  {

//		checkIfRestaurantExists(idRestaurant);

//		RestTemplate template = new RestTemplate();
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());

		URI newMenuURI = defaultemplate.postForLocation("http://localhost:8080/menu/", menu.getPrice());

		HttpEntity<String> restaurantEntity = new HttpEntity<String>("http://localhost:8080/restaurants/" + idRestaurant, reqHeaders);
		defaultemplate.exchange(newMenuURI +"/restaurant", HttpMethod.PUT, restaurantEntity, String.class);
	

		HttpEntity<String> ingredientsEntity;
		for (Ingredients i : menu.getIngredients()) {
			ingredientsEntity = new HttpEntity<String>("http://localhost:8080/ingredients/" + i.getId(), reqHeaders);
			defaultemplate.exchange(newMenuURI + "/ingredients", HttpMethod.POST, ingredientsEntity, String.class);
		}
	}

	@Override
	public List<Menu> getMenuByRestaurantId(Long idRestaurant) {
		String menuUrl = halTemplate.getForObject("http://localhost:8080/restaurants/{id}", PagedResources.class, idRestaurant).getLink("menu").getHref();
		Collection<Menu> menuHal = halTemplate.getForObject(menuUrl, PagedResources.class).getContent();

		if (menuHal.size() == 0)	return null;			//restaurant doesn't have any menu 

		System.out.println(menuHal.getClass());
		List<Menu> menu = mapper.convertValue(menuHal, new TypeReference<List<Menu>>() {});

		getIngredientsByMenu(menu);

		return menu;
	}
	
	private void getIngredientsByMenu(List<Menu> menu){
		for (Menu m : menu) {
			Collection<Ingredients> ingredientsHal = halTemplate.getForObject("http://localhost:8080/menu/{menuId}/ingredients", PagedResources.class, m.getId()).getContent();
			List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
			m.setIngredients(ingredients);
		}
	}
}
