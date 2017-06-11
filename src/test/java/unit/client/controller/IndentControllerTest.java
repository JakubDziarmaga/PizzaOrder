package unit.client.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class IndentControllerTest {
	private MockMvc mockMvc;

	@MockBean
	private RestaurantService restaurantServiceMock;

	@MockBean
	private IndentService indentServiceMock;

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
	public void add_indent() throws Exception{
		mockMvc.perform(get("/addindents/1/1")
				 .with(user("test").password("test").roles("USER")))
		 		 .andDo(print())
		 		 .andExpect(view().name("redirect:/user"))
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("/user"));		
		 
		 //verify(indentServiceMock, times(1)).addIndents(1L,1L);

	     verifyNoMoreInteractions(indentServiceMock);
	}
	
	@Test
	public void add_indent_without_authentication() throws Exception{
		mockMvc.perform(get("/addindents/1/1"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
		 
	     verifyNoMoreInteractions(indentServiceMock);
	}
	
	@Test
	public void add_indent_as_restaurantowner() throws Exception{
		mockMvc.perform(get("/addindents/1/1")
		 .with(user("test").password("test").roles("RESTAURANT_OWNER")))
		 .andDo(print())
		 .andExpect(status().isForbidden());		

		verifyNoMoreInteractions(indentServiceMock);
	}
	
	@Test
	public void pay_for_indent() throws Exception{		
		mockMvc.perform(get("/indent/pay/1")
				 .with(user("test").password("test").roles("USER")))
		 		 .andDo(print())
		 		 .andExpect(view().name("redirect:/user"))
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("/user"));		
		 
		 verify(indentServiceMock, times(1)).payForIndent(1L);
	     verifyNoMoreInteractions(indentServiceMock);
	}
	
	@Test
	public void pay_for_indent_without_authentication() throws Exception{
		mockMvc.perform(get("/indent/pay/1"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
		 
	     verifyNoMoreInteractions(indentServiceMock);
	}
	
	@Test
	public void pay_for_indent_as_restaurantowner() throws Exception{
		mockMvc.perform(get("/indent/pay/1")
		 .with(user("test").password("test").roles("RESTAURANT_OWNER")))
		 .andDo(print())
		 .andExpect(status().isForbidden());		

		verifyNoMoreInteractions(indentServiceMock);
	}
	
	@Test
	public void delete_indent() throws Exception{		
		mockMvc.perform(get("/indent/delete/1")
				 .with(user("test").password("test").roles("USER")))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("/user"));		
		 
		 verify(indentServiceMock, times(1)).deleteIndent(1L);
	     verifyNoMoreInteractions(indentServiceMock);
	}
	
	@Test
	public void delete_without_authentication() throws Exception{
		mockMvc.perform(get("/indent/delete/1"))
		 		 .andDo(print())
		 		 .andExpect(status().is3xxRedirection())
		 		 .andExpect(redirectedUrl("http://localhost/login"));		
		 
	     verifyNoMoreInteractions(indentServiceMock);
	}
	
	@Test
	public void delete_as_restaurantowner() throws Exception{
		mockMvc.perform(get("/indent/delete/1")
		 .with(user("test").password("test").roles("RESTAURANT_OWNER")))
		 .andDo(print())
		 .andExpect(status().isForbidden());		

		verifyNoMoreInteractions(indentServiceMock);
	}
}
