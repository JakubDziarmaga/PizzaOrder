package pizzaOrder.client.service.implementation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

	public List<Restaurant> getAllRestaurantsList() {
		return new ArrayList<Restaurant>(halTemplate.getForObject("http://localhost:8080/restaurants", PagedResources.class).getContent());
	}

	@Override
	public Restaurant getRestaurantById(Long restaurantId) {
		try {
			return halTemplate.getForObject("http://localhost:8080/restaurants/{id}", Restaurant.class,restaurantId);
		} catch (HttpClientErrorException e) {
			throw new RestaurantNotFoundException(restaurantId);
		}
	}

	//Check if restaurant with id = idRestaurant exists in DB
	//TODO potrzebne gdy jest getRestaurantById ?
	public void checkIfRestaurantExists(Long idRestaurant) {
		try {
			halTemplate.getForObject("http://localhost:8080/restaurants/{idRestaurant}", Restaurant.class, idRestaurant);
		} catch (HttpClientErrorException e) {
			throw new RestaurantNotFoundException(idRestaurant);
		}
	}

	@Override
	public void addRestaurant(Restaurant restaurant) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Long userId = halTemplate
				.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username)
				.getId();

		restaurant.setOwnerId(userId);
		halTemplate.postForObject("http://localhost:8080/restaurants", restaurant, Restaurant.class);		
	}

	@Override
	public Restaurant getRestaurantByOwnerId(Long ownerId) {
		Restaurant restaurant = halTemplate.getForObject("http://localhost:8080/restaurants/search/owner?ownerId={ownerId}", Restaurant.class, ownerId);

		return restaurant;
	}

}
