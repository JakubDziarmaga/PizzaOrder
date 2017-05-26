package pizzaOrder.client.controller;


import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import pizzaOrder.client.service.interfaces.UserService;
import pizzaOrder.client.validator.UserValidator;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;

@Controller
public class UserController {
	
    @Autowired 
    private UserService userService;
    
    @Autowired
    private UserValidator userValidator;
    
    
    /**
     * Show registration form
     */
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
    
        model.addAttribute("nonActivatedUser", new NonActivatedUser());

        return "register";
    }
    
    /**
	 * Add nonActrivatedUser
	 * @see pizzaOrder.client.validator.UserValidator.class
	 * @return register view if posted entity isn't valid
	 * @return "redirect:/" if entity was valid
     * @throws MessagingException 
	 */
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registration(@Valid NonActivatedUser nonActivatedUser, BindingResult bindingResult,Model model) throws MessagingException {
    	
        userValidator.validate(nonActivatedUser, bindingResult);
    	if(bindingResult.hasErrors()){
    		model.addAttribute("nonActivatedUser",nonActivatedUser);
    		return "register";
    	}
    	userService.addNonActivatedUser(nonActivatedUser);
        return "redirect:/";
    }

    /**
     * Show login page
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model,String error) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");
        
        return "login";
    }
    
    /**
     * Delete user data from temporaryUser table and post it in user table
     * Autologin
     * Redirect to home page 
     */
    @RequestMapping(value = "/activate/{nonActivatedUserId}")
    public String activateUser(Model model, @PathVariable("nonActivatedUserId") Long nonActivatedUserId){

    	userService.activateUser(nonActivatedUserId);

        return "redirect:/";

    }

    
    @RequestMapping(value = "/changeMail",method = RequestMethod.POST)
    public String activateUser(String newMail, Model model){

    	userService.changeMail(newMail);
    	
        return "redirect:/";

    }
    

}