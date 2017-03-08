package unit.client.controller;

import static org.assertj.core.api.Assertions.allOf;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pizzaOrder.Application;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.restaurant.Restaurant;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class HomeControllerTest {
	
	private MockMvc mockMvc;

	@MockBean
	private RestaurantService restaurantServiceMock;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy springSecurityFilter;
	
//	@MockBean
//	private UserDetailsService userDetailsService;
	 
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*").build();
	}
	
	@Test
	public void showHomePageWithListOfRestaurant() throws Exception {
		Restaurant first = new Restaurant(1L, "FirstName", "FirstCity", "FirstAddress",1111, 4L);
		Restaurant second = new Restaurant(2L, "SecondName", "SecondCity", "SecondAddress",2222, 5L);
 
        when(restaurantServiceMock.getAllRestaurantsList()).thenReturn(Arrays.asList(first, second));
 
        mockMvc.perform(get("/"))
		   		.andDo(print())
		   		.andExpect(status().isOk())
		   		.andExpect(view().name("home"))
                .andExpect(model().attribute("restaurants", hasSize(2)))
                .andExpect(model().attribute("restaurants", hasItem(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("name", is("FirstName")),
                                hasProperty("city", is("FirstCity")),
                                hasProperty("address", is("FirstAddress")),
                                hasProperty("phone", is(1111)),
                                hasProperty("ownerId", is(4L))
                        )
                )))
                .andExpect(model().attribute("restaurants", hasItem(
                        allOf(
                                hasProperty("id", is(2L)),
                                hasProperty("name", is("SecondName")),
                                hasProperty("city", is("SecondCity")),
                                hasProperty("address", is("SecondAddress")),
                                hasProperty("phone", is(2222)),
                                hasProperty("ownerId", is(5L))
                        )
                )))
                .andExpect(model().attributeDoesNotExist("actualUser"));
 
        verify(restaurantServiceMock, times(1)).getAllRestaurantsList();
        verifyNoMoreInteractions(restaurantServiceMock);
	}
	
	@Test
	public void showHomePageWithNoRestaurant() throws Exception {
		
        when(restaurantServiceMock.getAllRestaurantsList()).thenReturn(null);
 
        mockMvc.perform(get("/"))
		   		.andDo(print())
		   		.andExpect(status().isOk())
		   		.andExpect(view().name("home"))
                .andExpect(model().attribute("restaurants",nullValue() ))
                .andExpect(model().attributeDoesNotExist("actualUser"));
 
        verify(restaurantServiceMock, times(1)).getAllRestaurantsList();
        verifyNoMoreInteractions(restaurantServiceMock);
	}
	
	@Test 
//	@WithMockUser(username="testuser",roles="USER")

	public void showHomePageWithListOfRestaurantWithActivatedUser() throws Exception {
		Restaurant first = new Restaurant(1L, "FirstName", "FirstCity", "FirstAddress",1111, 4L);
		Restaurant second = new Restaurant(2L, "SecondName", "SecondCity", "SecondAddress",2222, 5L);
 
        when(restaurantServiceMock.getAllRestaurantsList()).thenReturn(Arrays.asList(first, second));

        
        mockMvc.perform(get("/")
        		.with(user("test").password("test").roles("USER"))) 	
		   		.andDo(print())
		   		.andExpect(status().isOk())
		   		.andExpect(view().name("home"))
                .andExpect(model().attribute("restaurants", hasSize(2)))
                .andExpect(model().attribute("restaurants", hasItem(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("name", is("FirstName")),
                                hasProperty("city", is("FirstCity")),
                                hasProperty("address", is("FirstAddress")),
                                hasProperty("phone", is(1111)),
                                hasProperty("ownerId", is(4L))
                        )
                )))
                .andExpect(model().attribute("restaurants", hasItem(
                        allOf(
                                hasProperty("id", is(2L)),
                                hasProperty("name", is("SecondName")),
                                hasProperty("city", is("SecondCity")),
                                hasProperty("address", is("SecondAddress")),
                                hasProperty("phone", is(2222)),
                                hasProperty("ownerId", is(5L))
                        )
                )))
                .andExpect(model().attributeExists("actualUser"));
 
        verify(restaurantServiceMock, times(1)).getAllRestaurantsList();
        verifyNoMoreInteractions(restaurantServiceMock);
	}
}
