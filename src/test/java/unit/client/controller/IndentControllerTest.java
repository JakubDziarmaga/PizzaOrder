//package unit.client.controller;
//
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.web.FilterChainProxy;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import pizzaOrder.Application;
//import pizzaOrder.client.exceptionHandler.MenuNotFoundException;
//import pizzaOrder.client.exceptionHandler.NotPermittedException;
//import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
//import pizzaOrder.client.service.interfaces.IndentService;
//import pizzaOrder.client.service.interfaces.MenuService;
//import pizzaOrder.client.service.interfaces.RestaurantService;
//import pizzaOrder.restService.model.indent.Indent;
//import pizzaOrder.restService.model.ingredients.Ingredients;
//import pizzaOrder.restService.model.menu.Menu;
//import pizzaOrder.restService.model.restaurant.Restaurant;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = Application.class)
//@WebAppConfiguration
//public class IndentControllerTest {
//	
//	private MockMvc mockMvc;
//
//	@MockBean
//	private RestaurantService restaurantServiceMock;
//	
//	@MockBean
//	private IndentService indentServiceMock;
//	
//	@MockBean
//	private MenuService menuServiceMock;
//
//	@Autowired
//	private WebApplicationContext webApplicationContext;
//
//	@Autowired
//	private FilterChainProxy springSecurityFilter;
//	
//	@Before
//	public void setUp() {
//		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*").build();
//	}
//	
//	@Test
//	public void redirect_to_homepage_when_restaurant_doesnt_exist() throws Exception{
//		
//		Mockito.doThrow(new RestaurantNotFoundException(1L)).doNothing().when(restaurantServiceMock).checkIfRestaurantExists(1L);;
//
//		 mockMvc.perform(get("/addindents/1/1")
//				 .with(user("test").password("test").roles("USER")))
//		 		 .andDo(print())
//		 		 .andExpect(view().name("redirect:/"))
//		 		 .andExpect(status().is3xxRedirection())
//		 		 .andExpect(redirectedUrl("/"))
//		 		 .andExpect(flash().attributeExists("error"))
//		 		 .andExpect(flash().attributeCount(1))
//		 		 .andExpect(flash().attribute("error", "Restaurant not found."));		
//		 
//		 verify(restaurantServiceMock, times(1)).checkIfRestaurantExists(1L);
//	     verifyNoMoreInteractions(restaurantServiceMock);
//	     verifyNoMoreInteractions(menuServiceMock);
//	     verifyNoMoreInteractions(indentServiceMock);
//
//	}
//	
//	@Test
//	public void redirect_to_homepage_when_menu_doesnt_exist() throws Exception{
//		
//		Mockito.doThrow(new MenuNotFoundException(1L)).doNothing().when(menuServiceMock).checkIfMenuExists(1L);
//
//		 mockMvc.perform(get("/addindents/1/1")
//				 .with(user("test").password("test").roles("USER")))
//		 		 .andDo(print())
//		 		 .andExpect(view().name("redirect:/"))
//		 		 .andExpect(status().is3xxRedirection())
//		 		 .andExpect(redirectedUrl("/"))
//		 		 .andExpect(flash().attributeExists("error"))
//		 		 .andExpect(flash().attributeCount(1))
//		 		 .andExpect(flash().attribute("error", "Menu not found."));	
//		 
//	
//		 verify(restaurantServiceMock, times(1)).checkIfRestaurantExists(1L);
//	     verifyNoMoreInteractions(restaurantServiceMock);
//		 verify(menuServiceMock, times(1)).checkIfMenuExists(1L);
//	     verifyNoMoreInteractions(menuServiceMock);
//	     verifyNoMoreInteractions(indentServiceMock);
//	}
//	
//	@Test
//	public void redirect_to_homepage_when_menu_doesnt_belong_to_restaurant() throws Exception{
//		
//		Mockito.doThrow(new NotPermittedException()).doNothing().when(menuServiceMock).checkIfMenuBelongsToRestaurant(1L,1L);
//
//		 mockMvc.perform(get("/addindents/1/1")
//				 .with(user("test").password("test").roles("USER")))
//		 		 .andDo(print())
//		 		 .andExpect(view().name("redirect:/"))
//		 		 .andExpect(status().is3xxRedirection())
//		 		 .andExpect(redirectedUrl("/"))
//		 		 .andExpect(flash().attributeExists("error"))
//		 		 .andExpect(flash().attributeCount(1))
//		 		 .andExpect(flash().attribute("error", "You have no permission."));		
//		 
//		 verify(restaurantServiceMock, times(1)).checkIfRestaurantExists(1L);
//	     verifyNoMoreInteractions(restaurantServiceMock);
//		 verify(menuServiceMock, times(1)).checkIfMenuExists(1L);
//		 verify(menuServiceMock, times(1)).checkIfMenuBelongsToRestaurant(1L,1L);
//	     verifyNoMoreInteractions(menuServiceMock);
//	     verifyNoMoreInteractions(indentServiceMock);
//	}
//	
//	@Test
//	public void add_indent() throws Exception{
//		
//
//		 mockMvc.perform(get("/addindents/1/1")
//				 .with(user("test").password("test").roles("USER")))
//		 		 .andDo(print())
//		 		 .andExpect(view().name("redirect:/user"))
//		 		 .andExpect(status().is3xxRedirection())
//		 		 .andExpect(redirectedUrl("/user"));		
//		 
//		 verify(restaurantServiceMock, times(1)).checkIfRestaurantExists(1L);
//	     verifyNoMoreInteractions(restaurantServiceMock);
//		 verify(menuServiceMock, times(1)).checkIfMenuExists(1L);
//		 verify(menuServiceMock, times(1)).checkIfMenuBelongsToRestaurant(1L,1L);
//	     verifyNoMoreInteractions(menuServiceMock);
//		 verify(indentServiceMock, times(1)).addIndents(1L,1L);
//	     verifyNoMoreInteractions(indentServiceMock);
//	}
//	
//
//
//}
