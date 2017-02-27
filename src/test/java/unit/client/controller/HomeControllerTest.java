package unit.client.controller;

import static org.hamcrest.Matchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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
// @ContextConfiguration(locations = {"classpath:testContext.xml",
// "classpath:exampleApplicationContext-web.xml"})
@WebAppConfiguration
//WORKS
public class HomeControllerTest {

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
	public void showHomepageForAnonymousUser() throws Exception {
		mockMvc.perform(get("/"))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(view().name("home"))
			   .andExpect(model().attributeExists("restaurants"))
			   .andExpect(model().attributeDoesNotExist("actualUser"));
	}
	@Test
	public void showHomepageForAuthorisedUser() throws Exception{
		mockMvc.perform(get("/")
			   .with(user("test").password("test").roles("USER")))   	
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(view().name("home"))
			   .andExpect(model().attributeExists("restaurants"))
			   .andExpect(model().attributeExists("actualUser"));
	}


}
	
	
	
	
	
	
	
	
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {HomeController.class})
//@WebAppConfiguration
////public class HomeControllerTest {
//
//	private MockMvc mockMvc;
//	
//	@Test
//	public void showAppropriateView() throws Exception{
//		
//		mockMvc.perform(get("http://localhost:8080/")).andExpect(status().isOk());//.andExpect(view().name("home"));
//	}
//	
//	
//}
