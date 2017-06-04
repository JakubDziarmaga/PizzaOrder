package unit.client.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pizzaOrder.Application;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class RestaurantControllerTest {
	private MockMvc mockMvc;

	@MockBean
	private RestaurantService restaurantServiceMock;
	
	@MockBean
	private MenuService menuServiceMock;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy springSecurityFilter;

	 
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*").build();
	}
	
	@Test
	public void show_restaurant_page_with_user_authentication() throws Exception{
		final Long restaurantId = 4L;
		Restaurant restaurant = new Restaurant();
        when(restaurantServiceMock.getRestaurantById(restaurantId)).thenReturn(restaurant);
        
        Menu firstMenu = new Menu();
        firstMenu.setId(1L);
        Menu secondMenu = new Menu();
        firstMenu.setId(2L);
        List<Menu> menuList = new ArrayList<Menu>();
        menuList.add(firstMenu);
        menuList.add(secondMenu);

        when(menuServiceMock.getMenuByRestaurantId(restaurantId)).thenReturn(menuList);

        
        mockMvc.perform(get("/restaurant/{restaurantId}",restaurantId)
			   .with(user("test").password("test").roles("USER")))
        	   .andDo(print())
        	   .andExpect(status().isOk())
        	   .andExpect(view().name("restaurant"))
        	   .andExpect(model().attribute("restaurant",restaurant))
        	   .andExpect(model().attribute("menu",menuList))
        	   .andExpect(model().attributeExists("actualUser"));
 
        verify(restaurantServiceMock, times(1)).getRestaurantById(restaurantId);
        verifyNoMoreInteractions(restaurantServiceMock);
        
        verify(menuServiceMock, times(1)).getMenuByRestaurantId(restaurantId);
        verifyNoMoreInteractions(restaurantServiceMock);
	}
	
	@Test
	public void show_restaurant_page_without_authentication() throws Exception{
		final Long restaurantId = 4L;
		Restaurant restaurant = new Restaurant();
        when(restaurantServiceMock.getRestaurantById(restaurantId)).thenReturn(restaurant);
        
        Menu firstMenu = new Menu();
        firstMenu.setId(1L);
        Menu secondMenu = new Menu();
        firstMenu.setId(2L);
        List<Menu> menuList = new ArrayList<Menu>();
        menuList.add(firstMenu);
        menuList.add(secondMenu);

        when(menuServiceMock.getMenuByRestaurantId(restaurantId)).thenReturn(menuList);

        
        mockMvc.perform(get("/restaurant/{restaurantId}",restaurantId))
        	   .andDo(print())
        	   .andExpect(status().isOk())
        	   .andExpect(view().name("restaurant"))
        	   .andExpect(model().attribute("restaurant",restaurant))
        	   .andExpect(model().attribute("menu",menuList))
        	   .andExpect(model().attributeDoesNotExist("actualUser"));
 
        verify(restaurantServiceMock, times(1)).getRestaurantById(restaurantId);
        verifyNoMoreInteractions(restaurantServiceMock);
        
        verify(menuServiceMock, times(1)).getMenuByRestaurantId(restaurantId);
        verifyNoMoreInteractions(restaurantServiceMock);

	}
}
