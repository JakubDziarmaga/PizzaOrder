package PizzaOrder.RestService;

import org.hamcrest.Matcher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import pizzaOrder.Application;
import pizzaOrder.client.service.IndentService;
import pizzaOrder.client.service.RestaurantService;
import pizzaOrder.client.service.UserService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes =  Application.class )

@WebAppConfiguration
public class NewTest {

	private MockMvc mockMvc;

	@Autowired
	private RestaurantService restaurantServiceMock;

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	private FilterChainProxy springSecurityFilter;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter).build();
	}

	@Test
	public void findById_TodoEntryFound_ShouldAddTodoEntryToModelAndRenderViewTodoEntryView() throws Exception {
       	Restaurant found = new Restaurant(1L, "DaGrasso", "Gliwice", "Krupnicza",123456789, 1L);
 
        when(restaurantServiceMock.getRestaurantById(1L)).thenReturn(found);
 
        mockMvc.perform(get("/restaurant/1")
        		.with(user("testuser").password("testuser").roles("USER")))
                .andDo(print())
				.andExpect(status().isOk())
			    .andExpect(view().name("restaurant"))
                .andExpect(model().attribute("restaurant", hasProperty("id", is(1L))))
                .andExpect(model().attribute("restaurant", hasProperty("name", is("DaGrasso"))))
                .andExpect(model().attribute("restaurant", hasProperty("city", is("Gliwice"))))
                .andExpect(model().attribute("restaurant", hasProperty("address", is( "Krupnicza"))))
                .andExpect(model().attribute("restaurant", hasProperty("phone", is(123456789))))
                .andExpect(model().attribute("restaurant", hasProperty("ownerId", is(1L))));;
 
        verify(restaurantServiceMock, times(1)).getRestaurantById(1L);
        verifyNoMoreInteractions(restaurantServiceMock);
    }


}
