package pizzaOrder.client.service.interfaces;

import java.util.List;

import pizzaOrder.restService.model.ingredients.Ingredients;

public interface IngredientService {
	List<Ingredients> getAllIngredients();
}
