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

import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class RestaurantRepositoryTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void getRestaurantById() throws Exception {
		mockMvc.perform(get("/restaurants/{idRestaurant}", 25L))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(content().contentType("application/hal+json;charset=UTF-8"))
			   .andExpect(jsonPath("$.id", is(25)))
			   .andExpect(jsonPath("$.name", is("telepizza")))
			   .andExpect(jsonPath("$.city", is("gliwice")))
			   .andExpect(jsonPath("$.address", is("gggdsa 12")))
			   .andExpect(jsonPath("$.phone", is(5)))				
			   .andExpect(jsonPath("$._links.self.href", is("http://localhost/restaurants/25")))
			   .andExpect(jsonPath("$._links.restaurant.href", is("http://localhost/restaurants/25")))
			   .andExpect(jsonPath("$._links.menu.href", is("http://localhost/restaurants/25/menu")))
			   .andExpect(jsonPath("$._links.indent.href", is("http://localhost/restaurants/25/indent")));
	}

	@Test
	public void getRestaurantByUserId() throws Exception {
		 mockMvc.perform(get("/restaurants/search/owner?ownerId={idOwner}", 47))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/hal+json;charset=UTF-8"))
				.andExpect(jsonPath("$.id", is(25)))
				.andExpect(jsonPath("$.name", is("telepizza")))
				.andExpect(jsonPath("$.city", is("gliwice")))
				.andExpect(jsonPath("$.address", is("gggdsa 12")))
				.andExpect(jsonPath("$.phone", is(5)))				
				.andExpect(jsonPath("$._links.self.href", is("http://localhost/restaurants/25")))
				.andExpect(jsonPath("$._links.restaurant.href", is("http://localhost/restaurants/25")))
				.andExpect(jsonPath("$._links.menu.href", is("http://localhost/restaurants/25/menu")))
				.andExpect(jsonPath("$._links.indent.href", is("http://localhost/restaurants/25/indent")));
	}

	@Test
	public void postRestaurant() throws Exception {
		Restaurant restaurant = new Restaurant();
		restaurant.setAddress("testAdress");
		restaurant.setCity("testCity");
		restaurant.setName("testName");
		restaurant.setPhone(1234567);
		
		ObjectMapper mapper = new ObjectMapper();

		mockMvc.perform(post("/restaurants").content(mapper.writeValueAsString(restaurant))).andExpect(status().isCreated());
	}

	@Test
	public void deleteRestaurant() throws Exception {
		mockMvc.perform(delete("/restaurants/1")).andExpect(status().isNoContent()); //it's ok. Restaraunt was deleted
	}

	@Test
	public void getListOfRestaurants() throws Exception {
		mockMvc.perform(get("/restaurants")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("application/hal+json;charset=UTF-8"));
	}

}
