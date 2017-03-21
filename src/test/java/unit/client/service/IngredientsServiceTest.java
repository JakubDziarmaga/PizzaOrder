package unit.client.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.service.implementation.IngredientServiceImpl;
import pizzaOrder.restService.model.ingredients.Ingredients;

public class IngredientsServiceTest {

	@Mock(name = "halTemplate")
	private RestTemplate halTemplate;	
	
	@Spy
	private ObjectMapper mapper;
	
    @InjectMocks
	private IngredientServiceImpl ingredientsService;
    

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void get_all_ingredients() throws Exception{
    	Ingredients first = new Ingredients(1L, "szynka");
    	Ingredients second = new Ingredients(1L, "pieczarki");
    	
    	PagedResources<Ingredients> ingredientsPagedResources = new PagedResources<Ingredients>(
    			Arrays.asList(first,second), new PageMetadata(1, 0, 10));
    	

    	Mockito.when
    			(halTemplate
    			.getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class)))
                .thenReturn(ingredientsPagedResources);
    	
    	
    	List<Ingredients> ingredientsList = ingredientsService.getAllIngredients();
    	
		verify(halTemplate, times(1)).getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class));
		verifyNoMoreInteractions(halTemplate);

		Assert.assertNotNull(ingredientsList);
		Assert.assertEquals(2, ingredientsList.size());
		Assert.assertEquals(ingredientsList.get(0).getName(),first.getName());		
		Assert.assertEquals(ingredientsList.get(1).getName(),second.getName());		

		Assert.assertEquals(ingredientsList.get(0).getName(),first.getName());
		Assert.assertEquals(ingredientsList.get(1).getName(),second.getName());
    	
    }
	
}
