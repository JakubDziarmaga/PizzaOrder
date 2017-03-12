package client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.Application;
import pizzaOrder.client.exceptionHandler.MenuNotFoundException;
import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.client.service.implementation.MenuServiceImpl;
import pizzaOrder.client.service.implementation.UserServiceImpl;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;
import pizzaOrder.security.SecurityService;
import pizzaOrder.security.UserSecurityService;

@ContextConfiguration(classes = { Application.class})
public class MenuServiceTest {

	@Mock(name = "halTemplate")
	private RestTemplate halTemplate;
	
	@Mock(name = "defaultTemplate")
	private RestTemplate defaulTemplate;
	
	@Spy//(name = "halObjectMapper")
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
		Ingredients thirdIngredient = new Ingredients("boczek");
		
		testMenu = new Menu(5L, 10.0, null, Arrays.asList(firstIngredient,secondIngredient), null);
		testRestaurant = new Restaurant(1L, "testName", "testCity", "testAddress", 123456, 10L);
	}
	
	@Test(expected = MenuNotFoundException.class)
	public void throws_MenuNotFoundException_when_menu_doesnt_exist_in_db() throws Exception{
		Mockito.when(defaulTemplate.getForObject(Matchers.anyString(), Matchers.eq(Menu.class), anyLong())).thenThrow(HttpClientErrorException.class);

		menuService.checkIfMenuExists(1L);
	}
	
	@Test
	public void add_menu() throws Exception{
		Mockito.when(defaulTemplate.postForLocation(Matchers.anyString(), anyDouble())).thenReturn(new URI("testUri"));
		
		menuService.addMenu(testMenu, testRestaurant.getId());
		verify(defaulTemplate, times(1)).postForLocation(Matchers.anyString(), Matchers.anyDouble());
		verify(defaulTemplate, times(1)).exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.<HttpEntity<?>> any(), Mockito.<Class<?>> any());
		verify(defaulTemplate, times(2)).exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.<HttpEntity<?>> any(), Mockito.<Class<?>> any());
		verifyNoMoreInteractions(defaulTemplate);
	}
	
	@Test
	public void return_null_when_restaraurant_doesnt_have_any_menu() throws Exception{
    	String menuUrl = "testMenuUrl";
    	Menu firstMenu = new Menu(10.0);
    	Menu secondMenu = new Menu(20.0);

    	PagedResources<Menu> restaurantHal = new PagedResources<Menu>(Collections.emptyList(),  new PageMetadata(1, 0, 10));
    	restaurantHal.add(new Link(menuUrl,"menu"));    	
    	
    	
    	Mockito.when
    			(halTemplate
    			.getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class),anyLong()))
                .thenReturn(restaurantHal);
		
    	PagedResources<Menu> menuHal = new PagedResources<Menu>(Collections.emptyList(), new PageMetadata(1, 0, 10));   
    	Arrays.asList(firstMenu,secondMenu);
    	
    	System.out.println(menuHal.getContent());
    	
    	Mockito.when(halTemplate.getForObject(menuUrl, PagedResources.class)).thenReturn(menuHal);
    	
    	
//		menuService.getMenuByRestaurantId(testRestaurant.getId());
		assertNull(menuService.getMenuByRestaurantId(testRestaurant.getId()));
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
    	
//    	List<Menu> menuList = new ArrayList<>();
//    	menuList.add(firstMenu);
//    	menuList.add(secondMenu);

    	
//    	Mockito.when(mapper.convertValue(isA(Menu[].class),Mockito.<TypeReference<List<Menu>>> any())).thenReturn(menuList);
//    	Mockito.when(mapper.convertValue(anyCollection(), Mockito.<TypeReference<List<Menu>>> anyObject())). then(Mockito.CALLS_REAL_METHODS);

    	
    	
		Ingredients firstIngredient = new Ingredients("szynka");
		Ingredients secondIngredient = new Ingredients("pieczarki");
		Ingredients thirdIngredient = new Ingredients("boczek");
    	PagedResources<Ingredients> firstIngredientsResource = new PagedResources<Ingredients>(Arrays.asList(firstIngredient,secondIngredient), new PageMetadata(1, 0, 10));       	
    	PagedResources<Ingredients> secondIngredientsResource = new PagedResources<Ingredients>(Arrays.asList(thirdIngredient), new PageMetadata(1, 0, 10));       	
    	
//    	Map<Long,List<Ingredients>> ingredientsMap = new HashMap<Long,List<Ingredients>> ();
//    	ingredientsMap.put(1L, Arrays.asList(firstIngredient,secondIngredient));
//    	ingredientsMap.put(2L, Arrays.asList(thirdIngredient));
    	
    	Mockito.when(halTemplate.getForObject(Matchers.startsWith("http://localhost:8080/menu/"), Matchers.eq(PagedResources.class),Matchers.eq(1L))).thenReturn(firstIngredientsResource);
    	Mockito.when(halTemplate.getForObject(Matchers.startsWith("http://localhost:8080/menu/"), Matchers.eq(PagedResources.class),Matchers.eq(2L))).thenReturn(secondIngredientsResource);
//    	Mockito.when(mapper.convertValue(anyListOf(Ingredients.class),Mockito.<TypeReference<List<Ingredients>>> anyObject())).thenReturn( Arrays.asList(firstIngredient,secondIngredient));


		List<Menu> menuList = menuService.getMenuByRestaurantId(testRestaurant.getId());
		
		verify(halTemplate, times(3)).getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class),anyLong());
		verify(halTemplate, times(1)).getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class));
		verifyNoMoreInteractions(halTemplate);
		verifyNoMoreInteractions(defaulTemplate);

		Assert.assertNotNull(menuList);
		Assert.assertEquals(2, menuList.size());
		assertThat(menuList.get(0).getIngredients(), is(Arrays.asList(firstIngredient,secondIngredient)));
		assertThat(menuList, hasItem(secondMenu));
		
	}
}
