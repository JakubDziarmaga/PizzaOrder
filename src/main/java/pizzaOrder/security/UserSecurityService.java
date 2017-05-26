package pizzaOrder.security;

import java.net.URI;

import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.users.User;

public interface UserSecurityService {
	
	URI save(NonActivatedUser user);
}