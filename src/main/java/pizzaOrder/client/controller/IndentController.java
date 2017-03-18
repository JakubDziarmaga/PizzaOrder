package pizzaOrder.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pizzaOrder.client.service.interfaces.IndentService;

@Controller
public class IndentController {
	
	@Autowired
	private IndentService indentService;
	
	/**
	 * Add indent to user cart
	 */
	@RequestMapping(value = "/addindents/{idRestaurant}/{idMenu}")
	public String addIndents(@PathVariable("idRestaurant") Long idRestaurant, @PathVariable("idMenu") Long idMenu) {

		indentService.addIndents(idRestaurant, idMenu);

		return "redirect:/user";
	}
	

	@RequestMapping(value = "/indent/pay/{id}", method = RequestMethod.GET) 
	public String payForIndent(@PathVariable("id") Long idIndent) {

		indentService.payForIndent(idIndent);
		
		return "redirect:/user";
	}
	

	@RequestMapping(value = "/indent/delete/{idIndent}", method = RequestMethod.GET)
	public String deleteIndent(@PathVariable("idIndent") Long idIndent) {

		indentService.deleteIndent(idIndent);
		
		return "redirect:/user";
	}
}
