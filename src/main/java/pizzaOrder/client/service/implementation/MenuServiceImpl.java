package pizzaOrder.client.service.implementation;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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
import pizzaOrder.restService.model.size.Size;

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
	
	/**
	 * @Throw MenuNotFoundException if Menu with id = idMenu doesn't exist in db
	 */
	@Override
	public void checkIfMenuExists(Long idMenu) {
		try {
//			defaultemplate.getForObject("http://localhost:8080/menu/{idMenu}", Menu.class, idMenu);
			defaultemplate.getForObject("https://pizzaindent.herokuapp.com/menu/{idMenu}", Menu.class, idMenu);

		} catch (HttpClientErrorException e) {
			throw new MenuNotFoundException(idMenu);
		}
	}

	/**
	 * @Throw NotPermittedException if Restaurant with id = idRestaurant doesn't have Menu with id = idMenu
	 */
	@Override
	public void checkIfMenuBelongsToRestaurant(Long idRestaurant, Long idMenu) {
//		String restaurantUrl = halTemplate
//				.getForObject("http://localhost:8080/menu/{idMenu}", PagedResources.class, idMenu).getLink("restaurant")
//				.getHref();
		String restaurantUrl = halTemplate
				.getForObject("https://pizzaindent.herokuapp.com/menu/{idMenu}", PagedResources.class, idMenu).getLink("restaurant")
				.getHref();
		if (!idRestaurant.equals(halTemplate.getForObject(restaurantUrl, Restaurant.class).getId()))
			throw new NotPermittedException();
	}

	/**
	 * Add menu to restaurant with id = idRestaurant
	 */
	@Override
	public String addMenu(Menu menu, Long idRestaurant)  {
		
		//Configure HttpHeaders
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());

		Menu tempMenu = new Menu();
		tempMenu.setNameMenu(menu.getNameMenu());
//		tempMenu.setPrice(menu.getPrice());
		//Post new Menu
//		URI newMenuURI = defaultemplate.postForLocation("http://localhost:8080/menu/", tempMenu);
		URI newMenuURI = defaultemplate.postForLocation("https://pizzaindent.herokuapp.com/menu/", tempMenu);

		//Add Restaurant to Menu entity
//		HttpEntity<String> restaurantEntity = new HttpEntity<String>("http://localhost:8080/restaurants/" + idRestaurant, reqHeaders);
		HttpEntity<String> restaurantEntity = new HttpEntity<String>("https://pizzaindent.herokuapp.com/restaurants/" + idRestaurant, reqHeaders);
		defaultemplate.exchange(newMenuURI +"/restaurant", HttpMethod.PUT, restaurantEntity, String.class);

		//Add Ingredient to Menu entity
		HttpEntity<String> ingredientsEntity;
		for (Ingredients i : menu.getIngredients()) {
//			ingredientsEntity = new HttpEntity<String>("http://localhost:8080/ingredients/" + i.getId(), reqHeaders);
			ingredientsEntity = new HttpEntity<String>("https://pizzaindent.herokuapp.com/ingredients/" + i.getId(), reqHeaders);
			defaultemplate.exchange(newMenuURI + "/ingredients", HttpMethod.POST, ingredientsEntity, String.class);
		}

		return newMenuURI.toString(); 
	}

	/**
	 * @return List of Menu which belongs to Restaurant with id = idRestaurant
	 * @return Null if Restaurant doesn't have any menu
	 */
	@Override
	public List<Menu> getMenuByRestaurantId(Long idRestaurant) {
		//Get Menu entity
//		String menuUrl = halTemplate.getForObject("http://localhost:8080/restaurants/{id}", PagedResources.class, idRestaurant).getLink("menu").getHref();
		String menuUrl = halTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants/{id}", PagedResources.class, idRestaurant).getLink("menu").getHref();

		Collection<Menu> menuHal = halTemplate.getForObject(menuUrl, PagedResources.class).getContent();

		if (menuHal.size() == 0)	return null;			
		
		//Convert entity to Menu.class
		List<Menu> menu = mapper.convertValue(menuHal, new TypeReference<List<Menu>>() {});

		getIngredientsByMenu(menu);
		getSizeByMenu(menu);
		
		return menu;
	}
	
	/**
	 * Get list of Ingredients whichc belongs to Menu
	 * Add it to Menu entity
	 */
	private void getIngredientsByMenu(List<Menu> menu){
		for (Menu m : menu) {
//			Collection<Ingredients> ingredientsHal = halTemplate.getForObject("http://localhost:8080/menu/{menuId}/ingredients", PagedResources.class, m.getId()).getContent();
			Collection<Ingredients> ingredientsHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/menu/{menuId}/ingredients", PagedResources.class, m.getId()).getContent();

			List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
			m.setIngredients(ingredients);
		}
	}
	
	@Override
	public void getSizeByMenu(List<Menu>  menu) {
		for (Menu m : menu) {
//			Collection<Size> sizeHal = halTemplate.getForObject("http://localhost:8080/menu/{id}/size", PagedResources.class, m.getId()).getContent();
			Collection<Size> sizeHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/menu/{id}/size", PagedResources.class, m.getId()).getContent();

			List<Size> sizeList = mapper.convertValue(sizeHal, new TypeReference<List<Size>>() {});
		
			m.setSize(sizeList);
		}			
	}
	
	@Override
	public void addSizeToMenu(List<Size> size, String menuURI){
		System.out.println(5);
		//Configure HttpHeaders
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());

		HttpEntity<String> menuEntity = new HttpEntity<String>(menuURI, reqHeaders);

		for(Size s:size){
			if(s.getName().isEmpty()||s.getPrice().equals(null)){
				System.out.println("null");
				continue;
			}
			//Post new Menu
//			URI newSizeURI = defaultemplate.postForLocation("http://localhost:8080/size/", s);
			URI newSizeURI = defaultemplate.postForLocation("https://pizzaindent.herokuapp.com/size/", s);


			defaultemplate.exchange(newSizeURI +"/menu", HttpMethod.PUT, menuEntity, String.class);	
		}



	}

}
