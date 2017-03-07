package pizzaOrder.client.service;

import java.util.List;

import pizzaOrder.restService.model.ingredients.Ingredients;

public interface IngredientService {
	List<Ingredients> getAllIngredients();
}
