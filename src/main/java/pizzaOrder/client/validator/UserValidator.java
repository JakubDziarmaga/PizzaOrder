package pizzaOrder.client.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;

@Component
public class UserValidator implements Validator {
	
	@Autowired
	@Qualifier(value="defaultTemplate")
	private RestTemplate template;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return NonActivatedUser.class.equals(clazz);

	}

	@Override
	public void validate(Object target, Errors errors) {
		NonActivatedUser user = (NonActivatedUser) target;	
		
		checkIfPhoneNumberIsValid(errors, user);
		checkIfPasswordEqualsPasswordConfirm(errors, user.getPassword(), user.getPasswordConfirm());
//		checkIfUsernameIsAlreadyTaken(errors, user, template);
//	TODO UNCOMMENT
	}
	
	private void checkIfPhoneNumberIsValid(Errors errors,NonActivatedUser user){
		if(user.getPhone() == null){
			errors.rejectValue("phone", "wrongPhoneNumber", new Object[]{"'phone'"}, "Phone number must have between 7 and 9 digits");		
			return;
		}
		if(user.getPhone() < 100000000 || user.getPhone() > 999999999) 
			errors.rejectValue("phone", "wrongPhoneNumber", new Object[]{"'phone'"}, "Phone number must have between 7 and 9 digits");		

	}

	private void checkIfPasswordEqualsPasswordConfirm(Errors errors, String password, String passwordConfirm) {
		if(!password.equals(passwordConfirm))			
			errors.rejectValue("passwordConfirm", "wrongConfirmPassword", new Object[]{"'passwordConfirm'"}, "Typed password aren't equal");		
	}

	private void checkIfUsernameIsAlreadyTaken(Errors errors, NonActivatedUser user, RestTemplate template) {
		
		//Check if user with username is in NonActivatedUser table
		try{
			template.getForEntity("http://localhost:8080/users/search/names?username={username}", NonActivatedUser.class, user.getUsername());//.getStatusCodeValue();
			errors.rejectValue("username", "duplicatedName", new Object[]{"'username'"}, "This username is already taken");
			return;
		}
		catch(HttpClientErrorException e){}	
		
		//Check if user with username is in User table
		try{
			template.getForEntity("http://localhost:8080/nonactivatedusers/search/names?username={username}", NonActivatedUser.class, user.getUsername());//.getStatusCodeValue();
			errors.rejectValue("username", "duplicatedName", new Object[]{"'username'"}, "This username is already taken");
			return;
		}
		catch(HttpClientErrorException e){}
	}

}
