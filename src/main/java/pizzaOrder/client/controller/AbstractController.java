package pizzaOrder.client.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

public abstract class AbstractController {

	@Autowired
	protected RestTemplate template;
	@Autowired
	@Qualifier("configureHalObjectMapper")
	protected ObjectMapper mapper;

	public void getActualUser(Model model){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getPrincipal() != "anonymousUser") {
			org.springframework.security.core.userdetails.User actualUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
			model.addAttribute("actualUser", actualUser);
		}
	}
}
