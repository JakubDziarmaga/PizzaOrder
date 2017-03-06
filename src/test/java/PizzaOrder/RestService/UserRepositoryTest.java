package PizzaOrder.RestService;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.restService.model.users.User;
import pizzaOrder.restService.model.users.UserRepository;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserRepositoryTest {

	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp(){
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void getUserById() throws Exception {		
		mockMvc.perform(get("/users/{id}", 164L))
			   .andDo(print())
			   .andExpect(status().isOk())
               .andExpect(content().contentType("application/hal+json;charset=UTF-8"))
               .andExpect(jsonPath("$.id", is(164)))
               .andExpect(jsonPath("$.username",is("testuser")))
               .andExpect(jsonPath("$.mail",is("test@user")))
               .andExpect(jsonPath("$.phone",is(123456789)))
               .andExpect(jsonPath("$.role",is("USER")))
               .andExpect(jsonPath("$._links.self.href",is("http://localhost/users/164")))
               .andExpect(jsonPath("$._links.user.href",is("http://localhost/users/164")))
               .andExpect(jsonPath("$._links.indent.href",is("http://localhost/users/164/indent")));
		}
	
	@Test
	public void getUserByUsername() throws Exception {		
		mockMvc.perform(get("/users/search/names?username={username}", "testuser"))
			   .andDo(print())
			   .andExpect(status().isOk())
	           .andExpect(content().contentType("application/hal+json;charset=UTF-8"))
	           .andExpect(jsonPath("$.id", is(164)))
	           .andExpect(jsonPath("$.username",is("testuser")))
	           .andExpect(jsonPath("$.mail",is("test@user")))
	           .andExpect(jsonPath("$.phone",is(123456789)))
	           .andExpect(jsonPath("$.role",is("USER")))
	           .andExpect(jsonPath("$._links.self.href",is("http://localhost/users/164")))
	           .andExpect(jsonPath("$._links.user.href",is("http://localhost/users/164")))
	           .andExpect(jsonPath("$._links.indent.href",is("http://localhost/users/164/indent")));
	}
	
	@Test
	public void postUser() throws Exception {
		User user = new User();
		user.setMail("testmail");
		user.setUsername("testusername");
		user.setPassword("testpassword");
		user.setPhone(123);
		user.setRole("USER");
		ObjectMapper mapper = new ObjectMapper();

		mockMvc.perform(post("/users")
				.content(mapper.writeValueAsString(user)))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void deleteUser() throws Exception {
		mockMvc.perform(delete("/users/164"))
			   .andExpect(status().isNoContent());	// It's ok. Entity has been deleted
	}
	
	@Test
	public void getListOfUsers() throws Exception{
		mockMvc.perform(get("/users"))
			   .andDo(print())			   
			   .andExpect(status().isOk())
	           .andExpect(content().contentType("application/hal+json;charset=UTF-8"));
	}
	
}
