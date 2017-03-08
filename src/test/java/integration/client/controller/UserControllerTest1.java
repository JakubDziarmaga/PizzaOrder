package integration.client.controller;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import static org.hamcrest.Matchers.*;

import javax.annotation.Resource;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.Application;
import pizzaOrder.client.controller.HomeController;
import pizzaOrder.client.controller.UserController;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.users.User;
import pizzaOrder.security.SecurityService;
import pizzaOrder.security.UserSecurityService;

////@RunWith(SpringRunner.class)
////@WebMvcTest(UserController.class)
//////@SpringBootTest(classes=Application.class)
////@ContextConfiguration(classes = Application.class)
////@WebAppConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {Application.class})
//@WebAppConfiguration
//
//public class UserControllerTest1 {
//
//	private MockMvc mockMvc;
//	private UserController controller;
//	
////	@Before
////	public void setUp(){
////		controller = new UserController();
////	    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
////	}
//	
////	@Test
////     public void showingRegistrationFrom() throws Exception{		
////			
////         mockMvc.perform(get("http://localhost:8080/registration").accept(MediaType.TEXT_HTML))
////                 .andExpect(status().isOk())
////                 .andExpect(view().name("index"))
////                 .andExpect(model().attribute("user", hasProperty("id",nullValue())))
////                 .andExpect(model().attribute("user", hasProperty("username",nullValue())))
////                 .andExpect(model().attribute("user", hasProperty("password",nullValue())))
////                 .andExpect(model().attribute("user", hasProperty("mail",nullValue())))
////                 .andExpect(model().attribute("user", hasProperty("phone",is(0))))
////                 .andExpect(model().attribute("user", hasProperty("indent",nullValue())))
////                 .andExpect(model().attribute("user", hasProperty("role",nullValue())));
////    }
//	
//	@Test
//	public void postingUser() throws Exception{
//		controller = new UserController();
//	    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//		User user = new User();
//		user.setId(1L);
//		user.setIndent(null);
//		user.setMail("d");
//		user.setPassword("dd");
//		user.setPhone(1);
//		user.setRole("user");
//		user.setUsername("a");
//		
////		ObjectMapper mapper = new ObjectMapper();
////		String jsonContent = mapper.writeValueAsString(user);
//		
////		Model model = new  ExtendedModelMap();
////		controller.registration(user);
////		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//		//filter(model);
////		controller = new UserController();
////        controller.registration(user);
////        System.out.println(controller);
//        
////	    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//
////		 mockMvc.perform(post("http://localhost:8080/registration")).andExpect(status().isOk()); 
//
//		 mockMvc.perform(post("/registration")
//	                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//					 .param("id", "2")
//					 .param("username", "user")
//					 .param("password", "testPassword")
//					 .param("mail", "testMail")
//					 .param("role", "user")
//			 		 .param("phone", "4342")
//	                .sessionAttr("user", new User())
//	        )
//	                .andExpect(status().isOk());
//		 
//		 
////				 .sessionAttr("userForm", exampleEntity)
////				 .param("id", "2")
////				 .param("username", "user")
////				 .param("password", "testPassword")
////				 .param("mail", "testMail")
////				 .param("role", "user")
////		 		 .param("phone", "4342"))
////		 .andExpect(status().isOk()); 
////		  RequestBuilder request = post("http://localhost:8080/registration")
////					 .param("id", "2")
////					 .param("username", "user")
////					 .param("password", "testPassword")
////					 .param("mail", "testMail")
////					 .param("role", "user")
////			 		 .param("phone", "4342") ;
////
////		  System.out.println(request.toString());
////		  
////			    mockMvc
////			        .perform(request)
////			        .andDo(MockMvcResultHandlers.print())
////			        .andExpect(redirectedUrl("/"));
//			       
//	}
//
//
//
//}
