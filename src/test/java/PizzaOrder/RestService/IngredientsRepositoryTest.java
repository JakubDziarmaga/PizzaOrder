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

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.users.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class IngredientsRepositoryTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void getIngredientById() throws Exception {		
		mockMvc.perform(get("/ingredients/{id}", 1L))
			   .andDo(print())
			   .andExpect(status().isOk())
               .andExpect(content().contentType("application/hal+json;charset=UTF-8"))
               .andExpect(jsonPath("$.id", is(1)))
               .andExpect(jsonPath("$.name",is("szynka")))
               .andExpect(jsonPath("$._links.self.href",is("http://localhost/ingredients/1")))
               .andExpect(jsonPath("$._links.ingredients.href",is("http://localhost/ingredients/1")))
               .andExpect(jsonPath("$._links.menu.href",is("http://localhost/ingredients/1/menu")));
		}
	
	@Test
	public void postIgredient() throws Exception {
		Ingredients ingredient = new Ingredients();
		ingredient.setName("testName");
		
		ObjectMapper mapper = new ObjectMapper();

		mockMvc.perform(post("/ingredients")
				.content(mapper.writeValueAsString(ingredient)))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void deleteIngredient() throws Exception {
		mockMvc.perform(delete("/ingredients/1"))
			   .andExpect(status().isNoContent());	// It's ok. Entity has been deleted
	}
	
	@Test
	public void getListOfIngredients() throws Exception{
		mockMvc.perform(get("/ingredients"))
			   .andDo(print())			   
			   .andExpect(status().isOk())
	           .andExpect(content().contentType("application/hal+json;charset=UTF-8"));
	}
}
