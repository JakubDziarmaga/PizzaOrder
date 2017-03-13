package client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.Application;
import pizzaOrder.client.config.RestTemplateConfig;
import pizzaOrder.client.service.implementation.IndentServiceImpl;
import pizzaOrder.client.service.implementation.IngredientServiceImpl;
import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.client.service.interfaces.IngredientService;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

public class IngredientsServiceTest {

	@Mock(name = "halTemplate")
	private RestTemplate halTemplate;	
	
	@Spy//(name = "halObjectMapper")
	private ObjectMapper mapper;
	
    @InjectMocks
	private IngredientServiceImpl ingredientsService;
    

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void pay_for_indent() throws Exception{
    	Ingredients first = new Ingredients(1L, "szynka");
    	Ingredients second = new Ingredients(1L, "pieczarki");
    	
    	PagedResources<Ingredients> persistentEntityResource = new PagedResources<Ingredients>(
    			Arrays.asList(first,second), new PageMetadata(1, 0, 10));
    	

    	Mockito.when
    			(halTemplate
    			.getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class)))
                .thenReturn(persistentEntityResource);
    	
    	
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
