package pizzaOrder.client.controller;

import java.net.URI;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.client.exceptionHandler.IndentAlreadyPaid;
import pizzaOrder.client.exceptionHandler.IndentNotFoundException;
import pizzaOrder.client.exceptionHandler.MenuNotFoundException;
import pizzaOrder.client.exceptionHandler.NotPermittedException;
import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
public class IndentController {
	
	@Autowired
	private IndentService indentService;
	
	//Add indent to actual logged user
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
