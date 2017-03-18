package unit.client.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
public class AccessForUser {
	
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
	public void disable_showing_add_indents_page_with_restaurant_owner_role() throws Exception{
		mockMvc.perform(get("/addindents/{idRestaurant}/{idMenu}",1L,1L)
				 .with(user("test").password("test").roles("RESTAURANT_OWNER")))
		 		 .andDo(print())
		 		 .andExpect(status().is4xxClientError());						
	}
	
	@Test
	public void disable_showing_pay_for_indent_page_with_restaurant_owner_role() throws Exception{
		mockMvc.perform(get("/indent/pay/{id}",1L)
				 .with(user("test").password("test").roles("RESTAURANT_OWNER")))
		 		 .andDo(print())
		 		 .andExpect(status().is4xxClientError());						
	}
	@Test
	public void disable_showing_delete_indent_page_with_restaurant_owner_role() throws Exception{
		mockMvc.perform(get("/indent/delete/{idIndent}",1L)
				 .with(user("test").password("test").roles("RESTAURANT_OWNER")))
		 		 .andDo(print())
		 		 .andExpect(status().is4xxClientError());						
	}
	@Test
	public void disable_showing_user_page_with_restaurant_owner_role() throws Exception{
		mockMvc.perform(get("/user")
				 .with(user("test").password("test").roles("RESTAURANT_OWNER")))
		 		 .andDo(print())
		 		 .andExpect(status().is4xxClientError());						
	}

}
