package pizzaOrder.client.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.users.User;

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
		
		try{
		template.getForEntity("http://localhost:8080/users/search/names?username={username}", NonActivatedUser.class, user.getUsername()).getStatusCodeValue();
		errors.rejectValue("username", "duplicatedName", new Object[]{"'username'"}, "This username is already taken");
		}
		catch(HttpClientErrorException e){} 		//It's OK. No user with this username found in User table
												    //Not ok. Doesn't check if user is int nonactivatedusers
		try{
			template.getForEntity("http://localhost:8080/nonactivatedusers/search/names?username={username}", NonActivatedUser.class, user.getUsername()).getStatusCodeValue();
			errors.rejectValue("username", "duplicatedName", new Object[]{"'username'"}, "This username is already taken");
			}
		catch(HttpClientErrorException e){} 		//It's OK. No user with this username found in NonActivatedUser Table
	}

}
