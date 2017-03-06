package unit.client.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.net.URI;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pizzaOrder.Application;
import pizzaOrder.client.controller.UserController;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.users.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
// @ContextConfiguration(locations = {"classpath:testContext.xml",
// "classpath:exampleApplicationContext-web.xml"})
@WebAppConfiguration
@Transactional
public class UserControllerTest {

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
	public void showRegistrationPage() throws Exception {
		mockMvc.perform(get("/registration")).andExpect(status().isOk());
	}

	@Test
	public void postUser() throws Exception {	
		
		mockMvc.perform(post("/registration")
				.param("username", "testUsername")
				.param("password", "testPassword")
				.param("mail", "test@Mail")
				.param("role", "USER")
				.param("phone", "1234567")
				.sessionAttr("nonActivatedUser", new NonActivatedUser()))
				.andExpect(status().is3xxRedirection());
	}
	
	@Test
	public void postUserWithValidationErrors() throws Exception {	
		
		mockMvc.perform(post("/registration")
				.param("username", "testUser")
				.param("password", "testPassword")
				.param("mail", "testMail")
				.param("role", "USER")
				.param("phone", "1234567")
				.sessionAttr("nonActivatedUser", new NonActivatedUser()))
				.andExpect(status().isOk())
			    .andExpect(view().name("register"));
	}
	
	@Test
	public void goodLogin() throws Exception{
		mockMvc.perform(post("/login")
				.param("username", "testuser")
				.param("password", "testuser"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}
	
	@Test
	public void badLogin() throws Exception{
		mockMvc.perform(post("/login")
				.param("username", "wrongUser")
				.param("password", "wrongUsername"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error"));
	}
	
//	Entity isn't send to server
    @AfterClass
	public static void deleteEntity() throws Exception{
    	RestTemplate template = new RestTemplate();
		Long userId = template.getForObject("http://localhost:8080/nonactivatedusers/search/names?username=testUsername", NonActivatedUser.class).getId();
		template.delete("http://localhost:8080/nonactivatedusers/{userId}",userId);
	}

}