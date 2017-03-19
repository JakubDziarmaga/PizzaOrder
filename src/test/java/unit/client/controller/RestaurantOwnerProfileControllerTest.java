package unit.client.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
import org.springframework.web.context.WebApplicationContext;

import pizzaOrder.Application;
import pizzaOrder.client.controller.RestaurantOwnerProfileController;
import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.client.service.interfaces.IngredientService;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.client.validator.MenuValidator;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class RestaurantOwnerProfileControllerTest {

	private MockMvc mockMvc;

	@MockBean
	private RestaurantService restaurantServiceMock;
	
	@MockBean
	private MenuService menuServiceMock;
	
	@MockBean
	private IndentService indentServiceMock;
	
	@MockBean
	private IngredientService ingredientsSrviceMock;	
	
	@MockBean
	private MenuValidator menuValidator;
//
//	@InjectMocks
//	private RestaurantOwnerProfileController controller;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy springSecurityFilter;
	
	@Before
	public void setUp() {
//        MockitoAnnotations.initMocks(this);

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*").build();
	}
	
	@Test
	public void show_add_restaurant_form () throws Exception{
		
        mockMvc.perform(get("/addRestaurant")
			   .with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER")))
        	   .andDo(print())
        	   .andExpect(status().isOk())
        	   .andExpect(view().name("addRestaurant"))
               .andExpect(model().attributeExists("actualUser"))
               .andExpect(model().attributeExists("restaurant"))
               .andExpect(model().attribute("restaurant",  allOf(
            		   hasProperty("id", nullValue()),
            		   hasProperty("name", nullValue()),
            		   hasProperty("city", nullValue()),
            		   hasProperty("address", nullValue()),
            		   hasProperty("phone", is(0)),
            		   hasProperty("ownerId", nullValue())
            		   )));

        verifyNoMoreInteractions(restaurantServiceMock);
        verifyNoMoreInteractions(menuServiceMock);
        verifyNoMoreInteractions(indentServiceMock);
        verifyNoMoreInteractions(ingredientsSrviceMock);
	}
	
	@Test
	public void post_new_restaurant() throws Exception{
		mockMvc.perform(post("/addRestaurant")
				.with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER"))
				.param("name", "testName")
				.param("city", "testCity")
				.param("address", "testAdress")
				.param("phone", "1234567")
				.sessionAttr("restaurant", new Restaurant()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/restaurantOwner"));
		
        verify(restaurantServiceMock, times(1)).addRestaurant(Matchers.any(Restaurant.class));
        verifyNoMoreInteractions(restaurantServiceMock);
        verifyNoMoreInteractions(menuServiceMock);
        verifyNoMoreInteractions(indentServiceMock);
        verifyNoMoreInteractions(ingredientsSrviceMock);
	}
	
	@Test
	public void post_new_restaurant_with_validation_errors() throws Exception{
		mockMvc.perform(post("/addRestaurant")
				.with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER"))
				.param("name", "zz")		//error - Username length must be between 3 and 30.
				.param("city", "zz")		//error - Username length must be between 3 and 20.
				.param("address", "z")		//error - Username length must be between 3 and 30.
				.param("phone", "123456")	//error - Phone number must have between 7 and 9 digits
				.sessionAttr("restaurant", new Restaurant()))
				.andExpect(model().attributeHasFieldErrors("restaurant", "name"))
				.andExpect(model().attributeHasFieldErrors("restaurant", "city"))
				.andExpect(model().attributeHasFieldErrors("restaurant", "address"))
				.andExpect(model().attributeHasFieldErrors("restaurant", "phone"))
	        	.andExpect(status().isOk())
	            .andExpect(model().attributeExists("restaurant"))
	        	.andExpect(view().name("addRestaurant"));
		
//        verify(restaurantServiceMock, times(1)).addRestaurant(Matchers.any(Restaurant.class));
        verifyNoMoreInteractions(restaurantServiceMock);
        verifyNoMoreInteractions(menuServiceMock);
        verifyNoMoreInteractions(indentServiceMock);
        verifyNoMoreInteractions(ingredientsSrviceMock);
	}
	
	@Test
	public void show_add_menu_form () throws Exception{
		final Long idRestaurant = 8L;
		mockMvc.perform(get("/restaurantOwner/{idRestaurant}/addmenu",idRestaurant)
			   .with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER")))
        	   .andDo(print())
        	   .andExpect(status().isOk())
        	   .andExpect(view().name("addMenu"))
               .andExpect(model().attributeExists("menu"))
               .andExpect(model().attributeExists("ingredients"))
               .andExpect(model().attribute("ingredients", notNullValue()))
               .andExpect(model().attribute("menu",  allOf(
            		   hasProperty("id", nullValue()),
            		   hasProperty("price", nullValue()),
            		   hasProperty("restaurant", nullValue()),
            		   hasProperty("ingredients", nullValue()),
            		   hasProperty("indent", nullValue())
            		   )));
        verify(restaurantServiceMock, times(1)).getRestaurantById(Matchers.anyLong());
        verify(ingredientsSrviceMock, times(1)).getAllIngredients();
        verifyNoMoreInteractions(restaurantServiceMock);
        verifyNoMoreInteractions(menuServiceMock);
        verifyNoMoreInteractions(indentServiceMock);
        verifyNoMoreInteractions(ingredientsSrviceMock);
	}
	
	@Test
	public void post_new_menu() throws Exception{
		final Long idRestaurant = 8L;
		mockMvc.perform(post("/restaurantOwner/{idRestaurant}/addmenu",idRestaurant)
				.with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER"))
				.param("price", "333")
				.sessionAttr("menu", new Menu()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/restaurantOwner"));
		
        verify(restaurantServiceMock, times(1)).getRestaurantById(Matchers.anyLong());
        verify(menuServiceMock, times(1)).addMenu(Matchers.any(Menu.class),Matchers.anyLong());
        verifyNoMoreInteractions(restaurantServiceMock);
        verifyNoMoreInteractions(menuServiceMock);
        verifyNoMoreInteractions(indentServiceMock);
        verifyNoMoreInteractions(ingredientsSrviceMock);
	}
	
//	@Test
//	public void post_new_menu_with_validation_errors() throws Exception{
//		final Long idRestaurant = 8L;
//		
//		BindingResult result = mock(BindingResult.class);
//	    when(result.hasErrors()).thenReturn(true);
//	    
//		mockMvc.perform(post("/restaurantOwner/{idRestaurant}/addmenu",idRestaurant)
//				.with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER"))
//				.param("price", "-3")				//error - price must be >0.
//				.sessionAttr("menu", new Menu()))
//				.andExpect(view().name("addMenu"))
//				.andExpect(model().attributeExists("menu"))
//				.andExpect(model().attributeExists("ingredients"));
//		
//        verifyNoMoreInteractions(restaurantServiceMock);
//        verifyNoMoreInteractions(menuServiceMock);
//        verifyNoMoreInteractions(indentServiceMock);
//        verifyNoMoreInteractions(ingredientsSrviceMock);
//	}
}
