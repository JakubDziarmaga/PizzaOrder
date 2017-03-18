package pizzaOrder.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;

import pizzaOrder.security.SecurityService;

public abstract class AbstractController {
	
	@Autowired
	protected SecurityService securityService;

	/**
	 * Get actual user from SecurityContext
	 * If actual user exists add it to model
	 */
	public void getActualUser(Model model){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getPrincipal() != "anonymousUser") {
			User actualUser = (User) auth.getPrincipal();
			model.addAttribute("actualUser", actualUser);
		}
	}
}
