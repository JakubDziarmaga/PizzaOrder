package unit.client.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pizzaOrder.Application;
import pizzaOrder.client.controller.UserController;
import pizzaOrder.restService.model.users.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
// @ContextConfiguration(locations = {"classpath:testContext.xml",
// "classpath:exampleApplicationContext-web.xml"})
@WebAppConfiguration
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
				.param("username", "testuser")
				.param("password", "testPassword")
				.param("mail", "testMail")
				.param("role", "USER")
				.param("phone", "4342")
				.sessionAttr("userForm", new User()))
				.andExpect(status().is3xxRedirection());
	}
	@Test
	public void goodLogin() throws Exception{
		mockMvc.perform(post("/login")
				.param("username", "testuser")
				.param("password", "testPassword"))
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
	
    @AfterClass
	public static void deleteEntity() throws Exception{
    	RestTemplate template = new RestTemplate();
		Long userId = template.getForObject("http://localhost:8080/users/search/names?username=testuser", User.class).getId();
		template.delete("http://localhost:8080/users/{userId}",userId);
	}

}