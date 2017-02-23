package pizzaOrder.client.controller;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.Url;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Controller
public class IndentController {

	@RequestMapping("/user")
	public String showUserData(Model model) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
		User user = template.getForObject("http://localhost:8080/users/search/names?username={username}", User.class,
				username);
		model.addAttribute("user", user);

		String indentUrl = template.getForObject("http://localhost:8080/users/search/names?username={username}",
				PagedResources.class, username).getLink("indent").getHref();
		List<Indent> indentsHal = new ArrayList<Indent>(
				template.getForObject(indentUrl, PagedResources.class).getContent());
		if(indentsHal.size()==0)
			return "user";

		System.out.println(indentsHal.getClass().toString());
		List<Indent> indents = mapper.convertValue(indentsHal, new TypeReference<List<Indent>>() {
		});
		user.setIndent(indents);

		System.out.println(user.getIndent().get(0).getMenu());
		System.out.println(user.getIndent().get(0).isPaid());

		int i = 0;
		for (Indent indent : indents) {
			String menuUrl = mapper.convertValue(indentsHal.get(i), PagedResources.class).getLink("menu").getHref();
			System.out.println(menuUrl);
			// List<Menu> menuHal = new
			// ArrayList<Menu>(template.getForObject(menuUrl,
			// PagedResources.class).getContent());
			// System.out.println(menuHal.size());
			//
			// Menu menu = mapper.convertValue(menuHal, new
			// TypeReference<Menu>() { });
			Menu menu = template.getForObject(menuUrl, Menu.class);
			System.out.println(menu.getPrice());

			indent.setMenu(menu);
			System.out.println(indent.getMenu().getPrice());

			////////////

			String ingredientsUrl = template.getForObject(menuUrl, PagedResources.class).getLink("ingredients")
					.getHref();
			System.out.println(ingredientsUrl);
			List<Ingredients> ingredientsHal = new ArrayList<Ingredients>(
					template.getForObject(ingredientsUrl, PagedResources.class).getContent());
			List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
			menu.setIngredients(ingredients);
			System.out.println(menu.getIngredients().toString());
			System.out.println(indent.getDate());
			
			i++;
		}
		System.out.println(user.getIndent().get(0).getMenu());
		System.out.println(user.getIndent().get(0).isPaid());

		System.out.println(indents);
		System.out.println(indents.get(0).isPaid());
		model.addAttribute("indents", indents);

		if (auth.getPrincipal() != "anonymousUser") {
			org.springframework.security.core.userdetails.User actualUser = (org.springframework.security.core.userdetails.User) auth
					.getPrincipal();
			model.addAttribute("actualUser", actualUser);

			System.out.println(actualUser.getAuthorities().getClass());
			if(actualUser.getAuthorities().toString().equals("[RESTAURANT_OWNER]"))
				System.out.println("DZIALALAALALALAL");
		}

		return "user";
	}

	@RequestMapping(value = "/indent/pay/{id}", method = RequestMethod.GET)			//TODO check if actualUser is owner of this indent
	public String payForIndent(@PathVariable("id") Long id) {
		RestTemplate template = new RestTemplate();
		Indent indent = template.getForObject("http://localhost:8080/indents/{id}", Indent.class, id);
		indent.setPaid(true);
		template.put("http://localhost:8080/indents/{id}", indent, id);
		return "redirect:/user";
	}

	@RequestMapping(value = "/addindents/{idRestaurant}/{idMenu}")
	public String addIndents(@PathVariable("idRestaurant") Long idRestaurant,@PathVariable("idMenu") Long idMenu) throws JsonProcessingException, MalformedURLException, URISyntaxException, UnsupportedEncodingException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getPrincipal() == "anonymousUser") { 						// TODO zamienic na try catch
			return null;
		}
		String username = auth.getName();
		
		RestTemplate template = new RestTemplate();
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());
		Indent indent = new Indent();
		indent.setDate(new Date());
		URI newURI = template.postForLocation("http://localhost:8080/indents/", indent);
		System.out.println(newURI);
		
		
		//TODO Delete magicnumbers
		//TODO let only owner of indents delete indents
		
		HttpEntity<String> restaurantEntity = new HttpEntity<String>("http://localhost:8080/restaurants/"+idRestaurant, reqHeaders);
		template.exchange(newURI+"/restaurant",  HttpMethod.PUT, restaurantEntity, String.class);
		
		Long userLink=template.getForObject("http://localhost:8080/users/search/names?username={username}",Restaurant.class, username).getId();
		System.out.println(userLink);
		HttpEntity<String> userEntity = new HttpEntity<String>("http://localhost:8080/users/"+userLink, reqHeaders);		
		template.exchange(newURI+"/user",  HttpMethod.PUT, userEntity, String.class);
		
		HttpEntity<String> menuEntity = new HttpEntity<String>("http://localhost:8080/menu/"+idMenu, reqHeaders);		
		template.exchange(newURI+"/menu",  HttpMethod.PUT, menuEntity, String.class);
		
		return "redirect:/user";
	}
	
	@RequestMapping(value = "indent/delete/{idIndent}")
	public String deleteIndent(@PathVariable("idIndent") Long idIndent){
		RestTemplate template = new RestTemplate();
		template.delete("http://localhost:8080/indents/{idIndent}",idIndent);
		return "redirect:/user";
	}

}