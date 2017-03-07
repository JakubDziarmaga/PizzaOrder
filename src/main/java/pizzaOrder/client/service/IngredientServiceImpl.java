package pizzaOrder.client.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.restService.model.ingredients.Ingredients;

@Service
public class IngredientServiceImpl implements IngredientService {

	@Autowired
	RestTemplate template;
	
	@Autowired
	@Qualifier("halObjectMapper")
	ObjectMapper mapper;
	
	@Override
	public List<Ingredients> getAllIngredients() {
		List<Ingredients> ingredientsHal = new ArrayList<Ingredients>(template.getForObject("http://localhost:8080/ingredients", PagedResources.class).getContent());
		List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
		return ingredients;
	}

}
