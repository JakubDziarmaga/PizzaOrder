package unit.client.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.Application;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes =  Application.class )
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RestaurantOwnerControllerTest {
	
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy springSecurityFilter;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*").build();

	}
	@AfterClass
	public static void cleanUp(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());
		
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);

		RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));

		Long restaurantId = template.getForObject("http://localhost:8080/restaurants/search/owner?ownerId=110",Restaurant.class).getId();
		List<Menu> menuHal = new ArrayList<Menu>(template.getForObject("http://localhost:8080/restaurants/"+restaurantId+"/menu", PagedResources.class).getContent());
		List<Menu> menu = mapper.convertValue(menuHal, new TypeReference<List<Menu>>() { });
		for(Menu m : menu){
			template.delete("http://localhost:8080/menu/"+m.getId());
		}
		template.delete("http://localhost:8080/restaurants/"+restaurantId);
	}
	
	@Test
	public void test1RedirectToLoginPageUnauthorizedUser() throws Exception{
		mockMvc.perform(get("/restaurantowner")).andDo(print())
		   	   .andExpect(status().is3xxRedirection())
		   	   .andExpect(redirectedUrl("http://localhost/login"));
	}
	
	@Test
	public void test2RedirectUserWithNoRestaurantsToAddRestaurantPage() throws Exception{
		mockMvc.perform(get("/restaurantowner")			   
		   .with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER")))
		   .andDo(print())
		   .andExpect(status().is3xxRedirection())
	   	   .andExpect(redirectedUrl("/addRestaurant"));		
	}
	
	@Test
	public void test3ShowAddRestaurantForm() throws Exception{
		mockMvc.perform(get("/addRestaurant")			   
				   .with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER")))
				   .andDo(print())
				   .andExpect(model().attributeExists("restaurant"))
//				   .andExpect(model().attributeExists("actualUser"))
				   .andExpect(view().name("addRestaurant"))
				   .andExpect(status().isOk());

	}
	
	@Test
	public void test4PostNewRestaraunt() throws Exception{
		mockMvc.perform(post("/addRestaurant")
				.with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER"))
				.param("name", "testName")
				.param("city", "testCity")
				.param("adress", "testAdress")
				.param("phone", "1234567")
				.sessionAttr("restaurant", new Restaurant()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/restaurantowner"));
	}
	
//	@Test
//	public void test3.5PostNewRestaurantoutValidation() throws Exception{
	
	@Test
	public void test5ShowAddMenuPage() throws Exception{
		RestTemplate template = new RestTemplate();
		Long restaurantId = template.getForObject("http://localhost:8080/restaurants/search/owner?ownerId=110",Restaurant.class).getId();
		
		mockMvc.perform(get("/restaurantowner/{restaurantId}/addmenu",restaurantId)
				.with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER")))
				.andExpect(model().attributeExists("actualUser"))
				.andExpect(model().attributeExists("menu"))
				.andExpect(model().attributeExists("ingredients"))
				.andExpect(status().isOk())
				.andExpect(view().name("addMenu"));
	}
	
	@Test
	public void test6AddMenu() throws Exception{
		RestTemplate template = new RestTemplate();
		Long restaurantId = template.getForObject("http://localhost:8080/restaurants/search/owner?ownerId=110",Restaurant.class).getId();
		
		mockMvc.perform(post("/restaurantowner/{restaurantId}/addmenu",restaurantId)
				.with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER"))
		.param("price", "333")
		.param("ingredients", "1")

		.sessionAttr("menu", new Menu()))
		.andExpect(status().is3xxRedirection());
	}
	
	@Test
	public void test7ShowRestaurantOwnerPage() throws Exception{
		mockMvc.perform(get("/restaurantowner")			   
				   .with(user("testRestaurantOwner").password("testRestaurantOwner").roles("RESTAURANT_OWNER")))
				   .andDo(print())
				   .andExpect(model().attributeExists("actualUser"))
				   .andExpect(model().attributeExists("restaurant"))
				   .andExpect(model().attributeExists("menus"))
				   .andExpect(model().attributeExists("ingredients"))
				   .andExpect(model().attributeExists("indents"))
			       .andExpect(view().name("restaurantOwner"))
			       .andExpect(status().isOk());
				
				

	}

}
