package unit.client.service;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.exceptionHandler.MenuNotFoundException;
import pizzaOrder.client.exceptionHandler.NotPermittedException;
import pizzaOrder.client.service.implementation.MenuServiceImpl;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
public class MenuServiceTest {

	@Mock(name = "halTemplate")
	private RestTemplate halTemplate;
	
	@Mock(name = "defaultTemplate")
	private RestTemplate defaultTemplate;
	
	@Spy
	protected ObjectMapper mapper;
	
	@InjectMocks
	private MenuServiceImpl menuService;

	private Menu testMenu;
	private Restaurant testRestaurant;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		Ingredients firstIngredient = new Ingredients("szynka");
		Ingredients secondIngredient = new Ingredients("pieczarki");
		
		testMenu = new Menu(5L, 10.0, null, Arrays.asList(firstIngredient,secondIngredient), null);
		testRestaurant = new Restaurant();
	}
	
	@Test(expected = MenuNotFoundException.class)
	public void throws_MenuNotFoundException_when_menu_doesnt_exist_in_db() throws Exception{
		Mockito.when(defaultTemplate.getForObject(Matchers.anyString(), Matchers.eq(Menu.class), anyLong())).thenThrow(HttpClientErrorException.class);

		menuService.checkIfMenuExists(1L);
	}
	
	@Test
	public void check_if_menu_belongs_to_restaurant() throws Exception{
		
    	Long expectedRestaurantId = 4L;    	
    	Long expectedMenuId = 2L;    	

    	PagedResources<Menu> menuHal = new PagedResources<Menu>(Collections.emptyList(),  new PageMetadata(1, 0, 10));
    	menuHal.add(new Link("restaurantUrl","restaurant"));   
    	
    	Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class),eq(expectedMenuId))).thenReturn(menuHal);
    	Restaurant restaurant = new Restaurant();
    	Mockito.when(halTemplate.getForObject(Matchers.eq("restaurantUrl"), Matchers.eq(Restaurant.class))).thenReturn(restaurant);
		
		menuService.checkIfMenuBelongsToRestaurant(expectedRestaurantId,expectedMenuId);
		
		verify(halTemplate, times(1)).getForObject(Matchers.anyString(), eq(PagedResources.class), anyLong());
		verify(halTemplate, times(1)).getForObject(Matchers.anyString(), eq(Restaurant.class));
		verifyNoMoreInteractions(halTemplate);
	}	
	
	@Test(expected = NotPermittedException.class)
	public void throws_NotPermittedException_when_menu_doesnt_exist_in_db() throws Exception{
		
		Long expectedRestaurantId = 4L;    	
    	Long expectedMenuId = 2L;    
    	
    	PagedResources<Menu> menuHal = new PagedResources<Menu>(Collections.emptyList(),  new PageMetadata(1, 0, 10));
    	menuHal.add(new Link("restaurantUrl","restaurant"));   
    	
    	Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class),eq(expectedMenuId))).thenReturn(menuHal);
    	Restaurant restaurant = new Restaurant();									//Make sure restaurant id is different than expectedRestaurantId
    	Mockito.when(halTemplate.getForObject(Matchers.eq("restaurantUrl"), Matchers.eq(Restaurant.class))).thenReturn(restaurant);
		
		menuService.checkIfMenuBelongsToRestaurant(expectedRestaurantId,expectedMenuId);
	}
	
	@Test
	public void add_menu() throws Exception{
		Mockito.when(defaultTemplate.postForLocation(Matchers.anyString(), anyDouble())).thenReturn(new URI("testUri"));
		
		menuService.addMenu(testMenu, testRestaurant.getId());
		verify(defaultTemplate, times(1)).postForLocation(Matchers.anyString(), Matchers.anyDouble());
		verify(defaultTemplate, times(1)).exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.<HttpEntity<?>> any(), Mockito.<Class<?>> any());
		verify(defaultTemplate, times(2)).exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.<HttpEntity<?>> any(), Mockito.<Class<?>> any());
		verifyNoMoreInteractions(defaultTemplate);
	}
	
	@Test
	public void return_null_when_restaraurant_doesnt_have_any_menu() throws Exception{
    	String menuUrl = "testMenuUrl";
    	Menu firstMenu = new Menu();
    	Menu secondMenu = new Menu();

    	PagedResources<Menu> restaurantHal = new PagedResources<Menu>(Collections.emptyList(),  new PageMetadata(1, 0, 10));
    	restaurantHal.add(new Link(menuUrl,"menu"));        	
    	
    	Mockito.when
    			(halTemplate
    			.getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class),anyLong()))
                .thenReturn(restaurantHal);
		
    	PagedResources<Menu> menuHal = new PagedResources<Menu>(Collections.emptyList(), new PageMetadata(1, 0, 10));   
    	Arrays.asList(firstMenu,secondMenu);
    	    	
    	Mockito.when(halTemplate.getForObject(menuUrl, PagedResources.class)).thenReturn(menuHal);
    	
    	List<Menu> menuList = menuService.getMenuByRestaurantId(testRestaurant.getId());
    	
		verify(halTemplate, times(1)).getForObject(Matchers.anyString(), eq(PagedResources.class), anyLong());
		verify(halTemplate, times(1)).getForObject(Matchers.anyString(), eq(PagedResources.class));
		verifyNoMoreInteractions(halTemplate);
		assertNull(menuList);
	}
	@Test
	public void get_menuList_by_restaurant_id() throws Exception{
    	String menuUrl = "testMenuUrl";
    	Menu firstMenu = new Menu(1L, 10.0, null, null, null);
    	Menu secondMenu = new Menu(2L, 20.0, null, null, null);    	

    	PagedResources<Menu> restaurantHal = new PagedResources<Menu>(Collections.emptyList(),  new PageMetadata(1, 0, 10));
    	restaurantHal.add(new Link(menuUrl,"menu"));    	
    	    	
    	Mockito.when
    			(halTemplate
    			.getForObject(Matchers.startsWith("http://localhost:8080/restaurants/"), Matchers.eq(PagedResources.class),anyLong()))
                .thenReturn(restaurantHal);
		
    	PagedResources<Menu> menuHal = new PagedResources<Menu>(Arrays.asList(firstMenu,secondMenu), new PageMetadata(1, 0, 10));   
    	    	
    	Mockito.when(halTemplate.getForObject(menuUrl, PagedResources.class)).thenReturn(menuHal);  	
    	
		Ingredients firstIngredient = new Ingredients("szynka");
		Ingredients secondIngredient = new Ingredients("pieczarki");
		Ingredients thirdIngredient = new Ingredients("boczek");
		
		//Add ingredients to first menu
		List<Ingredients> firstMenuIngredients = new ArrayList<Ingredients> ();
		firstMenuIngredients.addAll(Arrays.asList(firstIngredient,secondIngredient));
		
		//Add ingredients to second menu
		List<Ingredients> secondMenuIngredients = new ArrayList<Ingredients> ();
		secondMenuIngredients.addAll(Arrays.asList(thirdIngredient));		
		
    	PagedResources<Ingredients> firstIngredientsResource = new PagedResources<Ingredients>(firstMenuIngredients, new PageMetadata(1, 0, 10));       	
    	PagedResources<Ingredients> secondIngredientsResource = new PagedResources<Ingredients>(secondMenuIngredients, new PageMetadata(1, 0, 10));       	
    	
    	Mockito.when(halTemplate.getForObject(Matchers.startsWith("http://localhost:8080/menu/"), Matchers.eq(PagedResources.class),Matchers.eq(1L))).thenReturn(firstIngredientsResource);
    	Mockito.when(halTemplate.getForObject(Matchers.startsWith("http://localhost:8080/menu/"), Matchers.eq(PagedResources.class),Matchers.eq(2L))).thenReturn(secondIngredientsResource);

    	//Call the method
		List<Menu> menuList = menuService.getMenuByRestaurantId(testRestaurant.getId());
		
		//Verify result
		firstMenu.setIngredients(firstMenuIngredients);
		
		verify(halTemplate, times(3)).getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class),anyLong());
		verify(halTemplate, times(1)).getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class));
		verifyNoMoreInteractions(halTemplate);
		verifyNoMoreInteractions(defaultTemplate);

		Assert.assertNotNull(menuList);
		Assert.assertEquals(2, menuList.size());
		//Assert.assertEquals(menuList.get(0).getPrice(),firstMenu.getPrice());		
		//Assert.assertEquals(menuList.get(1).getPrice(),secondMenu.getPrice());

		Assert.assertEquals(menuList.get(0).getIngredients().get(0).getName(),"szynka");
		Assert.assertEquals(menuList.get(0).getIngredients().get(1).getName(),"pieczarki");
		Assert.assertEquals(menuList.get(1).getIngredients().get(0).getName(),"boczek");

	}
}
