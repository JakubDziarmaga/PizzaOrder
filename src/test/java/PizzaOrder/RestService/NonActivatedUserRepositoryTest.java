package PizzaOrder.RestService;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.restService.model.users.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class NonActivatedUserRepositoryTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void getUserById() throws Exception {
		mockMvc.perform(get("/nonactivatedusers/{id}", 31L))
		.andDo(print()).andExpect(status().isOk())
				.andExpect(content()
				.contentType("application/hal+json;charset=UTF-8"))
				.andExpect(jsonPath("$.id", is(31)))
				.andExpect(jsonPath("$.username", is("user")))
				.andExpect(jsonPath("$.mail", is("mail")))
				.andExpect(jsonPath("$.phone", is(1234567)))
				.andExpect(jsonPath("$.role", is("USER")))
				.andExpect(jsonPath("$._links.self.href", is("http://localhost/nonactivatedusers/31")))
				.andExpect(jsonPath("$._links.nonActivatedUser.href", is("http://localhost/nonactivatedusers/31")));
	}

	@Test
	public void getUserByUsername() throws Exception {
		mockMvc.perform(get("/nonactivatedusers/search/names?username={username}", "user")).andDo(print())
				.andExpect(status().isOk()).andExpect(content()
				.contentType("application/hal+json;charset=UTF-8"))
				.andExpect(jsonPath("$.id", is(31)))
				.andExpect(jsonPath("$.username", is("user")))
				.andExpect(jsonPath("$.mail", is("mail")))
				.andExpect(jsonPath("$.phone", is(1234567)))
				.andExpect(jsonPath("$.role", is("USER")))
				.andExpect(jsonPath("$._links.self.href", is("http://localhost/nonactivatedusers/31")))
				.andExpect(jsonPath("$._links.nonActivatedUser.href", is("http://localhost/nonactivatedusers/31")));
	}

	@Test
	public void postUser() throws Exception {
		User user = new User();
		user.setMail("testmail");
		user.setUsername("testusername");
		user.setPassword("testpassword");
		user.setPhone(1234567);
		user.setRole("USER");
		ObjectMapper mapper = new ObjectMapper();

		mockMvc.perform(post("/nonactivatedusers").content(mapper.writeValueAsString(user))).andExpect(status().isCreated());
	}

	@Test
	public void deleteUser() throws Exception {
		mockMvc.perform(delete("/nonactivatedusers/4")).andExpect(status().isNoContent()); // It's ok. Entity has been deleted
	}

	@Test
		public void getListOfNonActivatedUsers() throws Exception{
			mockMvc.perform(get("/nonactivatedusers"))
				   .andDo(print())			   
				   .andExpect(status().isOk())
		           .andExpect(content().contentType("application/hal+json;charset=UTF-8"));
		}
}