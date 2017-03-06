package unit.client.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import pizzaOrder.Application;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.users.User;
import org.junit.runners.MethodSorters;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes =  Application.class )
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@WebAppConfiguration
public class IndentControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy springSecurityFilter;
	 
	private Long indentId ;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*").build();

	}
	
	@Test
	public void test1RedirectToLoginPageUnauthorizedUser() throws Exception{
		mockMvc.perform(get("/user")).andDo(print())
		   	   .andExpect(status().is3xxRedirection())
		   	   .andExpect(redirectedUrl("http://localhost/login"));
	}
	
	@Test	
	public void test2ShowUserPageWithNoIndents() throws Exception{
		mockMvc.perform(get("/user")
				.with(user("testuser").password("testuser").roles("USER")))
				.andDo(print())
				.andExpect(status().isOk())
			    .andExpect(view().name("user"));
	}
	
	@Test
	public void test3AddIndent() throws Exception{
		mockMvc.perform(get("/addindents/2/7")
			   .with(user("testuser").password("testuser").roles("USER")))
			   .andDo(print())
			   .andExpect(status().is3xxRedirection())
			   .andExpect(redirectedUrl("/user"));
	}
	
	@Test
	public void test4PayForIndent() throws Exception{
		RestTemplate template = new RestTemplate();
		indentId = template.getForObject("http://localhost:8080/indents/search/username?userId=164", Long.class);
	
		mockMvc.perform(get("/indent/pay/{indentId}",indentId)
			   .with(user("testuser").password("testuser").roles("USER")))
	   		   .andDo(print())
	   		   .andExpect(status().is3xxRedirection())
	   		   .andExpect(redirectedUrl("/user"));
	}
		
	@Test	
	public void test5ShowUserPageWithIndents() throws Exception{
		mockMvc.perform(get("/user")
				.with(user("testuser").password("testuser").roles("USER")))
				.andDo(print())
				.andExpect(status().isOk())
			    .andExpect(view().name("user"));
	}
	
	@Test
	public void test6RedirectToUserPageWhenIndentIsAlreadyPaid() throws Exception{
		RestTemplate template = new RestTemplate();
		indentId = template.getForObject("http://localhost:8080/indents/search/username?userId=164", Long.class);
	
		mockMvc.perform(get("/indent/pay/{indentId}",indentId)
			   .with(user("testuser").password("testuser").roles("USER")))
	   		   .andDo(print())
	   		   .andExpect(status().is3xxRedirection())
	   		   .andExpect(redirectedUrl("/user"))
	   		   .andExpect(flash().attributeExists("error"))
	   		   .andExpect(flash().attributeCount(1));
	}
	
	@Test
	public void test7RedirectToUserPageWhenIndentDoesntExist() throws Exception{
		mockMvc.perform(get("/indent/pay/99999")
				   .with(user("testuser").password("testuser").roles("USER")))
				   .andDo(print())
				   .andExpect(status().is3xxRedirection())
				   .andExpect(redirectedUrl("/user"))
				   .andExpect(flash().attributeExists("error"))
				   .andExpect(flash().attributeCount(1));
	}
	
	@Test
	public void test8RedirectToHomePageWhenRestaurantDoesntExist() throws Exception{
		mockMvc.perform(get("/addindents/9999999/2")
				   .with(user("zz").password("zz").roles("USER")))
				   .andDo(print())
				   .andExpect(status().is3xxRedirection())
				   .andExpect(redirectedUrl("/"))
				   .andExpect(flash().attributeExists("error"))
				   .andExpect(flash().attributeCount(1));
	}
	
	@Test
	public void test8RedirectToHomePageWhenMenuDoesntExist() throws Exception{
		mockMvc.perform(get("/addindents/2/99999")
				   .with(user("zz").password("zz").roles("USER")))
				   .andDo(print())
				   .andExpect(status().is3xxRedirection())
				   .andExpect(redirectedUrl("/"))
				   .andExpect(flash().attributeExists("error"))
				   .andExpect(flash().attributeCount(1));
	}
	
	@Test
	public void test9RedirectToUserPageWhenIndentDoesntExist() throws Exception{
		mockMvc.perform(get("/indent/delete/99999")
				   .with(user("test").password("test").roles("USER")))
				   .andDo(print())
				   .andExpect(status().is3xxRedirection())
				   .andExpect(redirectedUrl("/user"))
				   .andExpect(flash().attributeExists("error"))
				   .andExpect(flash().attributeCount(1));
	}
	
	@Test
	public void test9deleteIndent() throws Exception{
		RestTemplate template = new RestTemplate();
		indentId = template.getForObject("http://localhost:8080/indents/search/username?userId=164", Long.class);

		mockMvc.perform(get("/indent/delete/{indentId}",indentId)
			   .with(user("testuser").password("testuser").roles("USER")))
	   		   .andDo(print())
	   		   .andExpect(status().is3xxRedirection())
	   		   .andExpect(redirectedUrl("/user"));
	}
	
}
