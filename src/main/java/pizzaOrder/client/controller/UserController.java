package pizzaOrder.client.controller;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pizzaOrder.client.service.interfaces.UserService;
import pizzaOrder.client.validator.UserValidator;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;

@Controller
//@SessionAttributes("user")
public class UserController {//extends AbstractController{
	
    @Autowired 
    private UserService userService;
    
    @Autowired
    private UserValidator userValidator;
    
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
    
    	NonActivatedUser nonActivatedUser = new NonActivatedUser();        	  
        model.addAttribute("nonActivatedUser", nonActivatedUser);

        return "register";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@Valid NonActivatedUser nonActivatedUser, BindingResult bindingResult,Model model) throws MessagingException{
    	
        userValidator.validate(nonActivatedUser, bindingResult);

    	if(bindingResult.hasErrors()){
    		model.addAttribute("nonActivatedUser",nonActivatedUser);
    		return "register";
    	}
    	userService.addNonActivatedUser(nonActivatedUser);

        return "redirect:/";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");
   
//        if (logout != null)
//            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }
    
    /**
     * Delete user data from temporaryuser table and post it in user table
     * Autologin
     * Redirect to home page 
     */
    @RequestMapping(value = "/activate/{nonActivatedUserId}")
    public String activateUser(Model model, @PathVariable("nonActivatedUserId") Long nonActivatedUserId){

    	userService.activateUser(nonActivatedUserId);

        return "redirect:/";

    }


}