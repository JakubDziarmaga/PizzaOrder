package unit.client.controller;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import pizzaOrder.Application;
import pizzaOrder.client.controller.UserController;
import pizzaOrder.client.service.interfaces.UserService;
import pizzaOrder.client.validator.UserValidator;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserControllerTest {
	
	private MockMvc mockMvc;
	
	@MockBean
	private UserService userServiceMock;
	
	@MockBean
    private UserValidator userValidatorMock;
	
	@InjectMocks
	private UserController userController;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy springSecurityFilter;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*").build();
	}
	
	@Test
	public void show_registration_form() throws Exception{
        mockMvc.perform(get("/registration"))
        	   .andDo(print())
        	   .andExpect(status().isOk())
        	   .andExpect(view().name("register"))
               .andExpect(model().attributeExists("nonActivatedUser"))
               .andExpect(model().attribute("nonActivatedUser",  allOf(
            		   hasProperty("id", nullValue()),
            		   hasProperty("username", nullValue()),
            		   hasProperty("password", nullValue()),
            		   hasProperty("mail", nullValue()),
            		   hasProperty("phone", is(0)),
            		   hasProperty("role", nullValue())
            		   )));

        verifyNoMoreInteractions(userServiceMock);
        verifyNoMoreInteractions(userValidatorMock);
	}
	
	@Test
	public void post_new_user() throws Exception{
        mockMvc.perform(post("/registration")
        		.param("username", "testUsername")
        		.param("password", "testPassword")
        		.param("mail", "test@mail")
        		.param("phone", "123456789")
        		.param("role", "USER")
        		.sessionAttr("nonActivatedUser", new NonActivatedUser()))
		 		.andExpect(view().name("redirect:/"))
		 		.andExpect(status().is3xxRedirection())
		 		.andExpect(redirectedUrl("/"));

        verify(userServiceMock, times(1)).addNonActivatedUser(Matchers.any(NonActivatedUser.class));
        verify(userValidatorMock, times(1)).validate(Matchers.any(NonActivatedUser.class),Matchers.any(Errors.class));

        verifyNoMoreInteractions(userServiceMock);
        verifyNoMoreInteractions(userValidatorMock);
	}
	
	@Test
	public void post_new_user_with_validation_error() throws Exception{
		
	
        mockMvc.perform(post("/registration")
        		.param("username", "test")  //error - Username length must be between 6 and 20.
        		.param("password", "test")  //error - Password length must be between 6 and 20.
        		.param("mail", "")			//error - Not blank.
        		.param("phone", "123456")	//error - Phone number must have between 7 and 9 digits
        		.param("role", "")			//error - Not blank.
        		.sessionAttr("nonActivatedUser", new NonActivatedUser()))
        		.andExpect(model().attributeHasFieldErrors("nonActivatedUser", "username"))
        		.andExpect(model().attributeHasFieldErrors("nonActivatedUser", "password"))
        		.andExpect(model().attributeHasFieldErrors("nonActivatedUser", "mail"))
        		.andExpect(model().attributeHasFieldErrors("nonActivatedUser", "phone"))
        		.andExpect(model().attributeHasFieldErrors("nonActivatedUser", "role"));


        verify(userValidatorMock, times(1)).validate(Matchers.any(NonActivatedUser.class),Matchers.any(Errors.class));

        verifyNoMoreInteractions(userServiceMock);
        verifyNoMoreInteractions(userValidatorMock);
	}

    
	
	@Test
	public void activate_user() throws Exception{
		final Long idUser = 6L;
        mockMvc.perform(get("/activate/{nonActivatedUserId}",idUser))
		 		.andExpect(view().name("redirect:/"))
		 		.andExpect(status().is3xxRedirection())
		 		.andExpect(redirectedUrl("/"));

        verify(userServiceMock, times(1)).activateUser(Matchers.anyLong());

        verifyNoMoreInteractions(userServiceMock);
        verifyNoMoreInteractions(userValidatorMock);
	}
}
