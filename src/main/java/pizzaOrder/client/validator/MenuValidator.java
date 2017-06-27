package pizzaOrder.client.validator;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;

@Component
public class MenuValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Menu.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		Menu menu = (Menu) target;

		checkIfNameIsValid(errors, menu.getNameMenu());
//		checkPrice(errors, menu.getPrice());
		checkIfIngredientsListIsValid(errors, menu.getIngredients());
	}
	
	/**
	 * Name can't be null
	 * Name must have between 3 and 20 digits
	 */
	private void checkIfNameIsValid(Errors errors,String name) {

		if (name.isEmpty())
			errors.rejectValue("name", "nameIsNotValid", new Object[] { "'name'" },"Please enter the name");	
		else if(name.length()>20 || name.length()<3)
			errors.rejectValue("name", "nameIsNotValid", new Object[] { "'name'" },"Name must have between 3 and 20 digits");	
		
	}

	/**
	 * Price can't be null or negative
	 */
	private void checkPrice(Errors errors, Double price){
		
		if (price==null)
			errors.rejectValue("price", "nullPrice", new Object[] { "'price'" },"Please enter the price");		
		else if (price<0)
			errors.rejectValue("price", "negativePrice", new Object[] { "'price'" },"Price can't be negative");	
	}
	
	/**
	 * IngredientsList can't be empty
	 */
	private void checkIfIngredientsListIsValid(Errors errors, List<Ingredients> ingredientsList) {

		if (ingredientsList.isEmpty())
			errors.rejectValue("ingredients", "emptyIngredients", new Object[] { "'ingredients'" },"Choose at least one ingredient");	
	}
}
