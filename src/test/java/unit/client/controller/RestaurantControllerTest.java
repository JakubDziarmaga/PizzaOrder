package unit.client.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pizzaOrder.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
@WebAppConfiguration
public class RestaurantControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy springSecurityFilter;
	 
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*").build();
	}
	
	@Test
	public void showRestaurantPage() throws Exception{
	mockMvc.perform(get("/restaurant/1")
		   .with(user("zz").password("zz").roles("USER")))
		   .andDo(print())
		   .andExpect(status().isOk())
		   .andExpect(model().attributeExists("restaurant"))
		   .andExpect(model().attributeExists("menus"))
		   .andExpect(model().attributeExists("ingredients"))
		   .andExpect(model().attributeExists("actualUser"))
		   .andExpect(view().name("restaurant"));

	}
	
	@Test
	public void redirectToUserpageUnauthorizedUser() throws Exception{
		mockMvc.perform(get("/restaurant/1"))
			   .andDo(print())
			   .andExpect(status().is3xxRedirection())
		   	   .andExpect(redirectedUrl("http://localhost/login"));

	}
	
}