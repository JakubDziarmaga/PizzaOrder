package pizzaOrder.client.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.service.interfaces.IngredientService;
import pizzaOrder.restService.model.ingredients.Ingredients;

@Service
public class IngredientServiceImpl implements IngredientService {

	@Autowired
	@Qualifier("halTemplate")
	private RestTemplate halTemplate;
	
	@Autowired
	@Qualifier("halObjectMapper")
	private ObjectMapper mapper;
	
	/**
	 * @return List of all restaurant saved in db
	 */
	@Override
	public List<Ingredients> getAllIngredients() {
		List<Ingredients> ingredientsHal = new ArrayList<Ingredients>(halTemplate.getForObject("http://localhost:8080/ingredients", PagedResources.class).getContent());

		List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
		return ingredients;
	}

}
