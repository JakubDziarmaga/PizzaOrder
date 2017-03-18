package unit.client.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
public class AccessForAuthenticatedUsers {
	
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
	public void disable_adding_indent_without_authentication() throws Exception{
		mockMvc.perform(get("/addindents/1/1"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));				 
	}
	
	@Test
	public void disable_paying_for_indent_without_authentication() throws Exception{
		mockMvc.perform(get("/indent/pay/1"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
	}
	
	@Test
	public void disable_deleting_indent_without_authentication() throws Exception{
		mockMvc.perform(get("/indent/delete/1"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
	}
	
	@Test
	public void disable_showing_restaurant_page_without_authentication() throws Exception{
		mockMvc.perform(get("/restaurant/1"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
	}
	
	@Test
	public void disable_showing_restaurant_owner_page_without_authentication() throws Exception{
		mockMvc.perform(get("/restaurantowner"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
	}
	
	@Test
	public void disable_showing_add_restaurant_page_without_authentication() throws Exception{
		mockMvc.perform(get("/addRestaurant"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
	}
	
	@Test
	public void disable_posting_new_restaurant_without_authentication() throws Exception{
		mockMvc.perform(post("/addRestaurant"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
	}
	
	@Test
	public void disable_showing_add_menu_page_without_authentication() throws Exception{
		mockMvc.perform(get("/restaurantowner/1/addmenu"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
	}
	
	@Test
	public void disable_posting_new_menu_without_authentication() throws Exception{
		mockMvc.perform(post("/restaurantowner/1/addmenu"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
	}
	
	@Test
	public void disable_showing_user_profile_page_without_authentication() throws Exception{
		mockMvc.perform(get("/user"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
	}
}
