package unit.client.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.client.validator.UserValidator;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
@RunWith(SpringJUnit4ClassRunner.class)
public class UserValidatorTest {
	
	@InjectMocks
	private UserValidator validator;
	
	@Mock(name = "defaultTemplate")
	private RestTemplate defaultTemplate;
	

	
	@Test
	public void password_doesnt_match_confirm_password() throws Exception{
		
		NonActivatedUser user = new NonActivatedUser();
		user.setPassword("password");
		user.setPasswordConfirm("differentPassword");
		
        BindException errors = new BindException(user, "user");

        when(defaultTemplate.getForEntity(Matchers.anyString(), Matchers.eq(NonActivatedUser.class),anyString())).thenThrow(HttpClientErrorException.class);

		validator.validate(user, errors);
		
		assertTrue(errors.hasErrors());
		assertNotNull(errors.getFieldError("passwordConfirm"));
	}
	
	@Test
	public void username_exists_in_user_table() throws Exception{
		
		NonActivatedUser user = new NonActivatedUser();
		user.setPassword("password");
		user.setPasswordConfirm("password");			//password are the same
		
        BindException errors = new BindException(user, "user");

		validator.validate(user, errors);
		
		assertTrue(errors.hasErrors());
		assertNull(errors.getFieldError("passwordConfirm"));
		assertNotNull(errors.getFieldError("username"));
	}
	
	@Test
	public void username_exists_in_nonactivateduser_table() throws Exception{
		
		NonActivatedUser user = new NonActivatedUser();
		user.setPassword("password");
		user.setPasswordConfirm("password");			//password are the same
		
        BindException errors = new BindException(user, "user");
        
        //Entity doesn't exist in user table
        when(defaultTemplate.getForEntity(Matchers.startsWith("http://localhost:8080/users"), Matchers.eq(NonActivatedUser.class),anyString())).thenThrow(HttpClientErrorException.class);

		validator.validate(user, errors);
		
		assertTrue(errors.hasErrors());
		assertNull(errors.getFieldError("passwordConfirm"));
		assertNotNull(errors.getFieldError("username"));
	}

	@Test
	public void every_field_is_valid() throws Exception{
		
		NonActivatedUser user = new NonActivatedUser();
		user.setPassword("password");
		user.setPasswordConfirm("password");
		
        BindException errors = new BindException(user, "user");

        when(defaultTemplate.getForEntity(Matchers.anyString(), Matchers.eq(NonActivatedUser.class),anyString())).thenThrow(HttpClientErrorException.class);

		validator.validate(user, errors);
		
		assertFalse(errors.hasErrors());
	}
}
