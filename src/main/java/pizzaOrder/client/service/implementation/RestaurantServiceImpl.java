package pizzaOrder.client.service.implementation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.size.Size;
import pizzaOrder.restService.model.stars.Stars;
import pizzaOrder.restService.model.users.User;

@Service
public class RestaurantServiceImpl implements RestaurantService {
	
	@Autowired
	@Qualifier("halTemplate")
	private RestTemplate halTemplate;
	
	@Autowired
	@Qualifier("defaultTemplate")
	private RestTemplate defaultTemplate;
	
	@Autowired
	@Qualifier("halObjectMapper")
	private ObjectMapper mapper;
	
	/**
	 * @return List of all restaurants in db
	 */
	public List<Restaurant> getAllRestaurantsList() {
//		Collection<Restaurant> restaurantsHal = halTemplate.getForObject("http://localhost:8080/restaurants", PagedResources.class).getContent();
		Collection<Restaurant> restaurantsHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants", PagedResources.class).getContent();

		List<Restaurant> restaurantList = mapper.convertValue(restaurantsHal, new TypeReference<List<Restaurant>>() {});

		for(Restaurant restaurant:restaurantList){
//			Stars stars = defaultTemplate.getForObject("http://localhost:8080/restaurants/{restaurantId}/stars", Stars.class, restaurant.getId());
			Stars stars = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants/{restaurantId}/stars", Stars.class, restaurant.getId());

			restaurant.setStars(stars);
			System.out.println(stars.getMean());
		}
		return restaurantList;
//		return new ArrayList<Restaurant>(halTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants", PagedResources.class).getContent());
	}

	/**
	 * @return Restaurant with id = idRestaurant
	 * @throw RestaurantNotFoundException when Restaurant with id = idRestaurant doesn't exist in db
	 */
	@Override
	public Restaurant getRestaurantById(Long idRestaurant) {
		try {
//			return halTemplate.getForObject("http://localhost:8080/restaurants/{id}", Restaurant.class,idRestaurant);
			return halTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants/{id}", Restaurant.class,idRestaurant);
		} catch (HttpClientErrorException e) {
			throw new RestaurantNotFoundException(idRestaurant);
		}
	}

	/**
	 * Set ownerId to actualUser id
	 * Add Restaurant entity to db
	 */
	@Override
	public void addRestaurant(Restaurant restaurant) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
//		Long userId = halTemplate
//				.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username)
//				.getId();
		
		Long userId = halTemplate
				.getForObject("https://pizzaindent.herokuapp.com/users/search/names?username={username}", User.class, username)
				.getId();

		restaurant.setOwnerId(userId);
		Stars stars = new Stars();
//		URI starUrl = halTemplate.postForLocation("http://localhost:8080/stars", stars, Stars.class);
		URI starUrl = halTemplate.postForLocation("https://pizzaindent.herokuapp.com/stars", stars, Stars.class);

//		URI restaurantUrl = halTemplate.postForLocation("http://localhost:8080/restaurants", restaurant, Restaurant.class);
		URI restaurantUrl = halTemplate.postForLocation("https://pizzaindent.herokuapp.com/restaurants", restaurant, Restaurant.class);
		
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());

		HttpEntity<String> starsEntity = new HttpEntity<String>(starUrl.toString(), reqHeaders);
		defaultTemplate.exchange(restaurantUrl +"/stars", HttpMethod.PUT, starsEntity, String.class);
		
		HttpEntity<String> restaurantEntity = new HttpEntity<String>(restaurantUrl.toString(), reqHeaders);
		defaultTemplate.exchange(starUrl +"/restaurant", HttpMethod.PUT, restaurantEntity, String.class);

		
	}

	/**
	 * @return Restaurant which belongs to user with id = idOwner 
	 */
	@Override
	public Restaurant getRestaurantByOwnerId(Long idOwner) {
		
//		return halTemplate.getForObject("http://localhost:8080/restaurants/search/owner?ownerId={ownerId}", Restaurant.class, idOwner);
		return halTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants/search/owner?ownerId={ownerId}", Restaurant.class, idOwner);

	}

	@Override
	public List<Restaurant> getRestaurantsByCity(String city) {
//		Collection<Restaurant> restaurantsHal = halTemplate.getForObject("http://localhost:8080/restaurants/search/city?city={city}", PagedResources.class, city).getContent();
		Collection<Restaurant> restaurantsHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants/search/city?city={city}", PagedResources.class, city).getContent();

		List<Restaurant> restaurantList = mapper.convertValue(restaurantsHal, new TypeReference<List<Restaurant>>() {});

		for(Restaurant restaurant:restaurantList){
//			Stars stars = defaultTemplate.getForObject("http://localhost:8080/restaurants/{restaurantId}/stars", Stars.class, restaurant.getId());
			Stars stars = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants/{restaurantId}/stars", Stars.class, restaurant.getId());

			restaurant.setStars(stars);
			System.out.println(stars.getMean());
		}
		return restaurantList;
		

	}

	@Override
	public Stars getStarsByRestaurantId(Long idRestaurant) {
		try {
//			return defaultTemplate.getForObject("http://localhost:8080/restaurants/{id}/stars", Stars.class,idRestaurant);
			return defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants/{id}/stars", Stars.class,idRestaurant);

		} catch (HttpClientErrorException e) {
			throw new RestaurantNotFoundException(idRestaurant);
		}	
		
	}

	@Override
	public void addStar(Long restaurantId, int rating) {
	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		String userHref = halTemplate.getForObject("http://localhost:8080/users/search/names?username={username}", PagedResources.class, auth.getName()).getLink("self").getHref();
		String userHref = halTemplate.getForObject("https://pizzaindent.herokuapp.com/users/search/names?username={username}", PagedResources.class, auth.getName()).getLink("self").getHref();

		User user = defaultTemplate.getForObject(userHref, User.class);
		
//		String starsHref = halTemplate.getForObject("http://localhost:8080/restaurants/{restaurantId}/stars", PagedResources.class, restaurantId).getLink("stars").getHref();
		String starsHref = halTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants/{restaurantId}/stars", PagedResources.class, restaurantId).getLink("stars").getHref();

		Stars stars = defaultTemplate.getForObject(starsHref, Stars.class);
//		Collection<User> userHel = halTemplate.getForObject("http://localhost:8080/stars/{starId}/users", PagedResources.class, stars.getId()).getContent();
		Collection<User> userHel = halTemplate.getForObject("https://pizzaindent.herokuapp.com/stars/{starId}/users", PagedResources.class, stars.getId()).getContent();

		List<User> userList = mapper.convertValue(userHel, new TypeReference<List<User>>() {});

		if(userList.contains(user))			//actual user already voted
			return;
		
		
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());
		
		int amount = stars.getAmount();
		double mean = stars.getMean();		
		double sum = amount * mean;
		sum +=rating;
		amount++;
		stars.setMean(sum/amount);
		stars.setAmount(amount);
		defaultTemplate.put(starsHref, stars, Stars.class);
		
		HttpEntity<String> userEntity = new HttpEntity<String>(userHref, reqHeaders);
		defaultTemplate.exchange(starsHref +"/users", HttpMethod.POST, userEntity, String.class);
	}


}
