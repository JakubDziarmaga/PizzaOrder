package pizzaOrder.client.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;

@Component
public class UserValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return NonActivatedUser.class.equals(clazz);

	}

	@Override
	public void validate(Object target, Errors errors) {
		NonActivatedUser user = (NonActivatedUser) target;	
		RestTemplate template = new RestTemplate();
		
		checkIfUsernameIsAlreadyTaken(errors, user, template);
	}

	private void checkIfUsernameIsAlreadyTaken(Errors errors, NonActivatedUser user, RestTemplate template) {
		
		//Check if user with username is in NonActivatedUser table
		try{
			template.getForEntity("http://localhost:8080/users/search/names?username={username}", NonActivatedUser.class, user.getUsername()).getStatusCodeValue();
			errors.rejectValue("username", "duplicatedName", new Object[]{"'username'"}, "This username is already taken");
			return;
		}
		catch(HttpClientErrorException e){}	
		
		//Check if user with username is in User table
		try{
			template.getForEntity("http://localhost:8080/nonactivatedusers/search/names?username={username}", NonActivatedUser.class, user.getUsername()).getStatusCodeValue();
			errors.rejectValue("username", "duplicatedName", new Object[]{"'username'"}, "This username is already taken");
			return;
		}
		catch(HttpClientErrorException e){}
	}

}
