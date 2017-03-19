package unit.client.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;

import pizzaOrder.client.validator.MenuValidator;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;

public class MenuValidatorTest {

	private MenuValidator validator;
	
	@Before
	public void set_up(){
		validator = new MenuValidator();
	}
	
	@Test
	public void name_is_empty() throws Exception{
		
		Ingredients ingredient = new Ingredients();
		List<Ingredients> ingredientsList = new ArrayList<Ingredients>();
		ingredientsList.add(ingredient);
		
		Menu menu = new Menu();
		menu.setName("");						//Name is null
		menu.setPrice(3.0);						//Price is OK
		menu.setIngredients(ingredientsList);	//IngredientsList is OK
		
        BindException errors = new BindException(menu, "menu");

		validator.validate(menu, errors);
		
		assertTrue(errors.hasErrors());
		assertThat(errors.getAllErrors().size(),is(1));
		assertNotNull(errors.getFieldError("name"));
		assertThat(errors.getFieldError("name").getDefaultMessage(),is("Please enter the name"));
	}
	
	@Test
	public void name_is_too_long() throws Exception{
		
		Ingredients ingredient = new Ingredients();
		List<Ingredients> ingredientsList = new ArrayList<Ingredients>();
		ingredientsList.add(ingredient);
		
		Menu menu = new Menu();
		menu.setName("qwertyuioqwertyuiopqwertgyhujik");		//Name is too long
		menu.setPrice(3.0);										//Price is OK
		menu.setIngredients(ingredientsList);					//IngredientsList is OK
		
        BindException errors = new BindException(menu, "menu");

		validator.validate(menu, errors);
		
		assertTrue(errors.hasErrors());
		assertThat(errors.getAllErrors().size(),is(1));
		assertNotNull(errors.getFieldError("name"));
		assertThat(errors.getFieldError("name").getDefaultMessage(),is("Name must have between 3 and 20 digits"));
	}
	
	@Test
	public void name_is_too_short() throws Exception{
		
		Ingredients ingredient = new Ingredients();
		List<Ingredients> ingredientsList = new ArrayList<Ingredients>();
		ingredientsList.add(ingredient);
		
		Menu menu = new Menu();
		menu.setName("qw");										//Name is too short
		menu.setPrice(3.0);										//Price is OK
		menu.setIngredients(ingredientsList);					//IngredientsList is OK
		
        BindException errors = new BindException(menu, "menu");

		validator.validate(menu, errors);
		
		assertTrue(errors.hasErrors());
		assertThat(errors.getAllErrors().size(),is(1));
		assertNotNull(errors.getFieldError("name"));
		assertThat(errors.getFieldError("name").getDefaultMessage(),is("Name must have between 3 and 20 digits"));
	}
	
	@Test
	public void price_is_null() throws Exception{
		
		Ingredients ingredient = new Ingredients();
		List<Ingredients> ingredientsList = new ArrayList<Ingredients>();
		ingredientsList.add(ingredient);
		
		Menu menu = new Menu();
		menu.setName("qwertyuio");								//Name is OK
		menu.setIngredients(ingredientsList);					//IngredientsList is OK
																//Price is Null
        BindException errors = new BindException(menu, "menu");

		validator.validate(menu, errors);
		
		assertTrue(errors.hasErrors());
		assertThat(errors.getAllErrors().size(),is(1));
		assertNotNull(errors.getFieldError("price"));
		assertThat(errors.getFieldError("price").getDefaultMessage(),is("Please enter the price"));
	}
	
	@Test
	public void price_is_negative() throws Exception{
		
		Ingredients ingredient = new Ingredients();
		List<Ingredients> ingredientsList = new ArrayList<Ingredients>();
		ingredientsList.add(ingredient);
		
		Menu menu = new Menu();
		menu.setName("qwertyuio");									//Name is OK
		menu.setPrice(-3.0);										//Price is negative
		menu.setIngredients(ingredientsList);						//IngredientsList is OK
		
        BindException errors = new BindException(menu, "menu");

		validator.validate(menu, errors);
		
		assertTrue(errors.hasErrors());
		assertThat(errors.getAllErrors().size(),is(1));
		assertNotNull(errors.getFieldError("price"));
		assertThat(errors.getFieldError("price").getDefaultMessage(),is("Price can't be negative"));
	}
	
	@Test
	public void ingredientList_ist_empty() throws Exception{
		
		Menu menu = new Menu();
		menu.setName("qwertyuio");									//Name is OK
		menu.setPrice(3.0);											//Price is OK
		menu.setIngredients(new ArrayList<Ingredients>());			//IngredientsList is empty

        BindException errors = new BindException(menu, "menu");

		validator.validate(menu, errors);
		
		assertTrue(errors.hasErrors());
		assertThat(errors.getAllErrors().size(),is(1));
		assertNotNull(errors.getFieldError("ingredients"));
		assertThat(errors.getFieldError("ingredients").getDefaultMessage(),is("Choose at least one ingredient"));
	}
	
	@Test
	public void every_field_is_valid() throws Exception{
		
		Ingredients ingredient = new Ingredients();
		List<Ingredients> ingredientsList = new ArrayList<Ingredients>();
		ingredientsList.add(ingredient);
		
		Menu menu = new Menu();
		menu.setName("qwertyuio");
		menu.setPrice(3.0);
		menu.setIngredients(ingredientsList);
		
        BindException errors = new BindException(menu, "menu");

		validator.validate(menu, errors);
		
		assertFalse(errors.hasErrors());
	}
}
