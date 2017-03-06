package PizzaOrder.RestService;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

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

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.users.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class IndentRepositoryTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void getIndentById() throws Exception {		
		mockMvc.perform(get("/indents/{id}", 37L))
			   .andDo(print())
			   .andExpect(status().isOk())
               .andExpect(content().contentType("application/hal+json;charset=UTF-8"))
               .andExpect(jsonPath("$.id", is(37)))
               .andExpect(jsonPath("$.date",is("1990-10-21T18:11:00.000+0000")))
               .andExpect(jsonPath("$.paid",is(true)))
               .andExpect(jsonPath("$.formattedDate",is("21/10/1990 19:11:00")))
               .andExpect(jsonPath("$._links.self.href",is("http://localhost/indents/37")))
               .andExpect(jsonPath("$._links.indent.href",is("http://localhost/indents/37")))
               .andExpect(jsonPath("$._links.user.href",is("http://localhost/indents/37/user")))
               .andExpect(jsonPath("$._links.restaurant.href",is("http://localhost/indents/37/restaurant")))
               .andExpect(jsonPath("$._links.menu.href",is("http://localhost/indents/37/menu")));
		}
	
	@Test
	public void getIndentIdByUserId() throws Exception {		
		mockMvc.perform(get("/indents/search/username?userId={userId}", 137L))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(content().string("37"));
	}
	
	@Test
	public void postIndent() throws Exception {
		Indent indent = new Indent();
		indent.setDate(new Date());
		ObjectMapper mapper = new ObjectMapper();

		mockMvc.perform(post("/indents")
				.content(mapper.writeValueAsString(indent)))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void deleteIndent() throws Exception {
		mockMvc.perform(delete("/indents/37"))
			   .andExpect(status().isNoContent());	// It's ok. Entity has been deleted
	}
	
	@Test
	public void getListOfIndents() throws Exception{
		mockMvc.perform(get("/indents"))
			   .andDo(print())			   
			   .andExpect(status().isOk())
	           .andExpect(content().contentType("application/hal+json;charset=UTF-8"));
	}
}
