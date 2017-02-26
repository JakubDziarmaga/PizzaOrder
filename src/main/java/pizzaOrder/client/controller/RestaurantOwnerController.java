package pizzaOrder.client.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
@SessionAttributes({"actualUser","ingredients"})
public class RestaurantOwnerController {

	@RequestMapping("/restaurantowner")
	public String findRestaurantByOwner(Model model) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		System.out.println(username);

		if (auth.getPrincipal() != "anonymousUser") {
			org.springframework.security.core.userdetails.User actualUser = (org.springframework.security.core.userdetails.User) auth
					.getPrincipal();
			model.addAttribute("actualUser", actualUser);
			
		}
		
		RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));

		Long userId = template
				.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username)
				.getId();
		System.out.println(userId);
		
		Restaurant restaurant;
		try{
		restaurant = template.getForObject("http://localhost:8080/restaurants/search/owner?ownerId={ownerId}", Restaurant.class, userId);
		System.out.println(restaurant);
		}
		catch(HttpClientErrorException e){
			return "redirect:/addRestaurant";
		}
		
		model.addAttribute("restaurant", restaurant);

		String menuUrl = template.getForObject("http://localhost:8080/restaurants/search/owner?ownerId={ownerId}",
				PagedResources.class, userId).getLink("menu").getHref();

		List<Menu> menu = new ArrayList<Menu>(template.getForObject(menuUrl, PagedResources.class).getContent());

		model.addAttribute("menus", menu);

		Map<Long, List<Ingredients>> ingredientsByMenu = new HashMap<Long, List<Ingredients>>();

		List<Menu> pojos = mapper.convertValue(menu, new TypeReference<List<Menu>>() {
		});

		int i = 0;
		for (Menu pojo : pojos) {
			String ingredientsUrl = mapper.convertValue(menu.get(i), PagedResources.class).getLink("ingredients")
					.getHref();
			List<Ingredients> ingredients = new ArrayList<Ingredients>(
					template.getForObject(ingredientsUrl, PagedResources.class).getContent());
			System.out.println(pojo.getId().getClass());
			ingredientsByMenu.put(pojo.getId(), ingredients);
			i++;
		}

		model.addAttribute("ingredients", ingredientsByMenu);

		
		
		Long restaurantId = restaurant.getId();
		System.out.println("restaurantId"+restaurantId);
		

		
		Collection<?> indentsHal =   template.getForObject("http://localhost:8080/restaurants/{restaurantId}/indent",PagedResources.class,restaurantId).getContent();

		List<PagedResources> indentsResource = mapper.convertValue(indentsHal, new TypeReference<List<PagedResources>>() {});
		List<Indent> indents = mapper.convertValue(indentsHal, new TypeReference<List<Indent> >() {});
		List<Indent> payedIndents = new ArrayList<Indent>();
		
		for(Indent indent : indents){
			System.out.println("INDENTS"+indent.getId());
			System.out.println("INDENTS"+indent.isPaid());
			System.out.println("INDENTS"+indent.getMenu());
			
			if(!indent.isPaid()) continue;
			
			User user = template.getForObject("http://localhost:8080/indents/{id}/user", User.class,indent.getId());
			indent.setUser(user);
			System.out.println("INDENTS"+indent.getUser().getUsername());
			
			Menu tempMenu = template.getForObject("http://localhost:8080/indents/{id}/menu", Menu.class,indent.getId());
			indent.setMenu(tempMenu);
			System.out.println("INDENTS"+indent.getMenu().getPrice());

			Collection<?> ingredientsHal =template.getForObject("http://localhost:8080/menu/{id}/ingredients", PagedResources.class,tempMenu.getId()).getContent();
			List<Ingredients> tempIngredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients> >() {});
			
			tempMenu.setIngredients(tempIngredients);
			System.out.println(tempMenu.getIngredients().get(0).getName());
			System.out.println(indent.getId());
			System.out.println(indent.isPaid());
			System.out.println(indent.getUser().getUsername());
			System.out.println(indent.getMenu().getPrice());
			System.out.println(indent.getDate());

			payedIndents.add(indent);
			
		}

		model.addAttribute("indents",payedIndents);
		
		return "restaurantOwner";
	}

	@RequestMapping(value = "/addRestaurant", method = RequestMethod.GET)
	public String addRestaurant(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (auth.getPrincipal() != "anonymousUser") {
//			org.springframework.security.core.userdetails.User actualUser = (org.springframework.security.core.userdetails.User) auth
//					.getPrincipal();
//			model.addAttribute("actualUser", actualUser);
//		}
		Restaurant restaurant = new Restaurant();
		String username = auth.getName();
		RestTemplate template = new RestTemplate();
		Long idOwner = template.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username).getId();

		restaurant.setOwnerId(idOwner);
		model.addAttribute("restaurant", restaurant);

		return "addRestaurant";
	}

	@RequestMapping(value = "/addRestaurant", method = RequestMethod.POST)
//	public String registration(@ModelAttribute("restaurant") Restaurant restaurant, BindingResult bindingResult,Model model) {
	public String registration(@Valid Restaurant restaurant, BindingResult bindingResult,Model model) {

		if(bindingResult.hasErrors()){
//			org.springframework.security.core.userdetails.User actualUser = (org.springframework.security.core.userdetails.User) 
//																			SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//			model.addAttribute("actualUser", actualUser);
			return "addRestaurant";
    	}

//		ObjectMapper mapper = new ObjectMapper();
//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		mapper.registerModule(new Jackson2HalModule());
//
//		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
//		converter.setObjectMapper(mapper);
//		
//		RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
		
		RestTemplate template = new RestTemplate();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		Long userId = template
				.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username)
				.getId();
		
		restaurant.setOwnerId(userId);
		template.postForObject("http://localhost:8080/restaurants", restaurant, Restaurant.class);
		

		return "redirect:/restaurantowner";
	}
	
	@RequestMapping(value="/restaurantowner/{idRestaurant}/addmenu", method = RequestMethod.GET) //TODO give access to owner of this restaurant, not all users
	public String addMenu(Model model,@PathVariable("idRestaurant") Long idRestaurant){
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getPrincipal() != "anonymousUser") {
			org.springframework.security.core.userdetails.User actualUser = (org.springframework.security.core.userdetails.User) auth
					.getPrincipal();
			model.addAttribute("actualUser", actualUser);
		}
		Menu menu = new Menu();
		model.addAttribute("menu",menu);
		
		RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
		List <Ingredients> ingredientsHal =new ArrayList<Ingredients>(template.getForObject("http://localhost:8080/ingredients", PagedResources.class).getContent());

		
		List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {	});


		for(Ingredients i:ingredients){
			System.out.println(i.getName());
		}
		model.addAttribute("ingredients", ingredients);



		return "addMenu";
	}
	
	@RequestMapping(value="/restaurantowner/{idRestaurant}/addmenu", method = RequestMethod.POST)
//	public String addMenu(@ModelAttribute("menu") Menu menu, BindingResult bindingResult,Model model,@PathVariable("idRestaurant") Long idRestaurant){
	public String addMenu(@Valid Menu menu, BindingResult bindingResult,Model model,@PathVariable("idRestaurant") Long idRestaurant ) throws URISyntaxException{

		System.out.println(menu.getIngredients());
		System.out.println(menu.getPrice());

		if(bindingResult.hasErrors()){
			return "addMenu";
		}
		
		
		System.out.println(menu.getPrice());
		for(Ingredients i:menu.getIngredients()){
			System.out.println("INGREDIENTS "+i.getId());
			System.out.println("INGREDIENTS "+i.getName());
		}

		System.out.println("A");		

		
		RestTemplate template = new RestTemplate();
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());
		
//		URI newURI = template.postForLocation("http://localhost:8080/menu/", menu.getId());
		System.out.println(menu.getPrice());		
		
		URI newURI = template.postForLocation("http://localhost:8080/menu/",menu.getPrice());
		
		System.out.println(newURI);		
		
		try{
		HttpEntity<String> restaurantEntity = new HttpEntity<String>("http://localhost:8080/restaurants/"+idRestaurant, reqHeaders);
		template.exchange(newURI+"/restaurant",  HttpMethod.PUT, restaurantEntity, String.class);
		}
		catch(HttpClientErrorException e){
			throw new RestaurantNotFoundException(idRestaurant);
		}
		
		HttpEntity<String> ingredientsEntity;
		
		for(Ingredients i  : menu.getIngredients()){
			ingredientsEntity = new HttpEntity<String>("http://localhost:8080/ingredients/"+i.getId(), reqHeaders);
			template.exchange(newURI+"/ingredients",  HttpMethod.POST, ingredientsEntity, String.class);

		}

		return "redirect:/restaurantowner";
	}

}
