package pizzaOrder.client.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.client.service.interfaces.UserService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.users.User;

@Controller
public class UserProfileController extends AbstractController{
	
	@Autowired
	private IndentService indentService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * Show user's data with list of indents
	 */	
	@RequestMapping("/user")
	public String showUserProfile(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		getActualUser(model);
		
		User user = userService.getUserByUsername(auth.getName());
		model.addAttribute("user",user);
		
		List<Indent> indentsList = indentService.getIndentsByUsername(auth.getName());
		model.addAttribute("indents", indentsList);
		
		return "user";
	}
	

	
	
}
