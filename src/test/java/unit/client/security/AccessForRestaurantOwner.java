package unit.client.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class AccessForRestaurantOwner {

	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy springSecurityFilter;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*")
				.build();
	}

	@Test
	public void disable_showing_restaurant_owner_page_with_user_role() throws Exception{
		mockMvc.perform(get("/indent")
			     .with(user("test").password("test").roles("RESTAURANT_OWNER")))
		 		 .andDo(print())
		 		 .andExpect(status().is4xxClientError());						
	}
	
	@Test
	public void disable_showing_add_restaurant_page_with_user_role() throws Exception{
		mockMvc.perform(get("/addRestaurant")			     
				 .with(user("test").password("test").roles("USER")))
		 		 .andDo(print())
		 		 .andExpect(status().is4xxClientError());				
	}

	@Test
	public void disable_posting_new_restaurant_with_user_role() throws Exception{
		mockMvc.perform(post("/addRestaurant")
				 .with(user("test").password("test").roles("USER")))
		 		 .andDo(print())
		 		 .andExpect(status().is4xxClientError());					
	}
	
	@Test
	public void disable_showing_add_menu_page_with_user_role() throws Exception{
		mockMvc.perform(get("/restaurantOwner/1/addmenu")
				 .with(user("test").password("test").roles("USER")))
		 		 .andDo(print())
		 		 .andExpect(status().is4xxClientError());					
	}
	
	@Test
	public void disable_posting_new_menu_with_user_role() throws Exception{
		mockMvc.perform(post("/restaurantOwner/1/addmenu")
				 .with(user("test").password("test").roles("USER")))
		 		 .andDo(print())
		 		 .andExpect(status().is4xxClientError());							
	}
}
