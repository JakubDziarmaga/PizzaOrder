package pizzaOrder.client.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.type.TypeReference;

import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Controller
public class RestaurantController extends AbstractController{

	@RequestMapping(value = "/restaurant/{restaurantId}")
	public String showRestaurantById(@PathVariable("restaurantId") Long restaurantId, Model model){

		try {
			Restaurant restaurant = template.getForObject("http://localhost:8080/restaurants/{id}", Restaurant.class,restaurantId);
			model.addAttribute("restaurant", restaurant);
		} catch (HttpClientErrorException e) {
			throw new RestaurantNotFoundException(restaurantId);
		}
		
		getActualUser(model);
		getMenuByRestaurantId(restaurantId, model);

		return "restaurant";
	}

	private void getMenuByRestaurantId(Long restaurantId, Model model) {
		String menuUrl = template.getForObject("http://localhost:8080/restaurants/{id}", PagedResources.class, restaurantId).getLink("menu").getHref();
		Collection<?> menuHal = template.getForObject(menuUrl, PagedResources.class).getContent();

		if (menuHal.size() == 0)	return;			//restaurant doesn't have any menu 

		List<Menu> menu = mapper.convertValue(menuHal, new TypeReference<List<Menu>>() {});

		for (Menu m : menu) {
			Collection<?> ingredientsHal = template.getForObject("http://localhost:8080/menu/{menuId}/ingredients", PagedResources.class, m.getId()).getContent();
			List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
			m.setIngredients(ingredients);
		}

		model.addAttribute("menu", menu);
	}
}
