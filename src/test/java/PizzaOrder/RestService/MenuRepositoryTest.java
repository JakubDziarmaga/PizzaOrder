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
import org.junit.BeforeClass;
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

import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.users.User;

	@RunWith(SpringRunner.class)
	@SpringBootTest
	@Transactional
	public class MenuRepositoryTest {

		private MockMvc mockMvc;
		
		@Autowired
		private WebApplicationContext webApplicationContext;

		@Before
		public void setUp(){
			mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		}

//		@Test
//		public void getMenuById() throws Exception {		
//			mockMvc.perform(get("/users/{id}", 104L))
//				   .andDo(print())
//				   .andExpect(status().isOk())
//	               .andExpect(content().contentType("application/hal+json;charset=UTF-8"))
//	               .andExpect(jsonPath("$.id", is(104)))
//	               .andExpect(jsonPath("$.username",is("test")))
//	               .andExpect(jsonPath("$.mail",is("test@test")))
//	               .andExpect(jsonPath("$.phone",is(123456789)))
//	               .andExpect(jsonPath("$.role",is("USER")))
//	               .andExpect(jsonPath("$._links.self.href",is("http://localhost/users/104")))
//	               .andExpect(jsonPath("$._links.user.href",is("http://localhost/users/104")))
//	               .andExpect(jsonPath("$._links.indent.href",is("http://localhost/users/104/indent")));
//			}
			
		@Test
		public void postMenu() throws Exception {
			Menu menu = new Menu();
			menu.setPrice(10.0);
			ObjectMapper mapper = new ObjectMapper();

			mockMvc.perform(post("/menu")
					.content(mapper.writeValueAsString(menu)))
					.andExpect(status().isCreated());
		}
		
		@Test
		public void deleteMenu() throws Exception {
			mockMvc.perform(delete("/menu/1"))
				   .andExpect(status().isNoContent());	// It's ok. Entity has been deleted
		}
		
		@Test
		public void getListOfMenu() throws Exception{
			mockMvc.perform(get("/menu"))
				   .andDo(print())			   
				   .andExpect(status().isOk())
		           .andExpect(content().contentType("application/hal+json;charset=UTF-8"));
		}
		
	}
