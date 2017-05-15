package pizzaOrder.client.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Service
public class RestaurantServiceImpl implements RestaurantService {
	
	@Autowired
	@Qualifier("halTemplate")
	private RestTemplate halTemplate;
	
	/**
	 * @return List of all restaurants in db
	 */
	public List<Restaurant> getAllRestaurantsList() {
//		return new ArrayList<Restaurant>(halTemplate.getForObject("http://localhost:8080/restaurants", PagedResources.class).getContent());
		return new ArrayList<Restaurant>(halTemplate.getForObject("https://limitless-eyrie-45489.herokuapp.com/restaurants", PagedResources.class).getContent());
	}

	/**
	 * @return Restaurant with id = idRestaurant
	 * @throw RestaurantNotFoundException when Restaurant with id = idRestaurant doesn't exist in db
	 */
	@Override
	public Restaurant getRestaurantById(Long idRestaurant) {
		try {
//			return halTemplate.getForObject("http://localhost:8080/restaurants/{id}", Restaurant.class,idRestaurant);
			return halTemplate.getForObject("https://limitless-eyrie-45489.herokuapp.com/restaurants/{id}", Restaurant.class,idRestaurant);
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
				.getForObject("https://limitless-eyrie-45489.herokuapp.com/users/search/names?username={username}", User.class, username)
				.getId();

		restaurant.setOwnerId(userId);
//		halTemplate.postForObject("http://localhost:8080/restaurants", restaurant, Restaurant.class);
		halTemplate.postForObject("https://limitless-eyrie-45489.herokuapp.com/restaurants", restaurant, Restaurant.class);		

	}

	/**
	 * @return Restaurant which belongs to user with id = idOwner 
	 */
	@Override
	public Restaurant getRestaurantByOwnerId(Long idOwner) {
		
//		return halTemplate.getForObject("http://localhost:8080/restaurants/search/owner?ownerId={ownerId}", Restaurant.class, idOwner);
		return halTemplate.getForObject("https://limitless-eyrie-45489.herokuapp.com/restaurants/search/owner?ownerId={ownerId}", Restaurant.class, idOwner);

	}
}
