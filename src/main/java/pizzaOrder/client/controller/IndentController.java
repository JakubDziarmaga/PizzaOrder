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
	@RequestMapping(value = "/addindents/{idRestaurant}/{idMenu}/{idSize}")
	public String addIndents(@PathVariable("idRestaurant") Long idRestaurant, @PathVariable("idMenu") Long idMenu, @PathVariable("idSize") Long idSize) {

		indentService.addIndents(idRestaurant, idMenu, idSize);

		return "redirect:/restaurant/{idRestaurant}";
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
	
	@RequestMapping(value = "/indent/delete/{idIndent}/{idCart}", method = RequestMethod.GET)
	public String deleteMenuFromCart(@PathVariable("idIndent") Long idIndent,@PathVariable("idCart") Long idCart) {

		indentService.deleteMenuFromCart(idIndent,idCart);
		
		return "redirect:/user";
	}
	
	@RequestMapping(value = "/{idRestaurant}/cart/{idCart}/increment", method = RequestMethod.GET)
	public String incrementMenuInCart(@PathVariable("idCart")Long idCart,@PathVariable("idRestaurant") Long restaurantId){

		indentService.incrementMenuInCart(idCart);
		
		return "redirect:/restaurant/{idRestaurant}";
	}
	
	@RequestMapping(value = "/{idRestaurant}/cart/{idCart}/decrement", method = RequestMethod.GET)
	public String decrementMenuInCart(@PathVariable("idCart")Long idCart,@PathVariable("idRestaurant") Long restaurantId){

		indentService.decrementMenuInCart(idCart);
		
		return "redirect:/restaurant/{idRestaurant}";
	}
}
