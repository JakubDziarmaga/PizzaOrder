package pizzaOrder.client.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Service
public class RestaurantServiceImpl implements RestaurantService {
	
	@Autowired
	RestTemplate template;

	public List<Restaurant> getAllRestaurantsList() {
		return new ArrayList<Restaurant>(template.getForObject("http://localhost:8080/restaurants", PagedResources.class).getContent());

	}

	@Override
	public Restaurant getRestaurantById(Long restaurantId) {
		try {
			return template.getForObject("http://localhost:8080/restaurants/{id}", Restaurant.class,restaurantId);
		} catch (HttpClientErrorException e) {
			throw new RestaurantNotFoundException(restaurantId);
		}
	}

	//Check if restaurant with id = idRestaurant exists in DB
	public void checkIfRestaurantExists(Long idRestaurant) {
		try {
			template.getForObject("http://localhost:8080/restaurants/{idRestaurant}", Restaurant.class, idRestaurant);
		} catch (HttpClientErrorException e) {
			throw new RestaurantNotFoundException(idRestaurant);
		}
	}

	@Override
	public void addRestaurant(Restaurant restaurant) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		Long userId = template
				.getForObject("http://localhost:8080/users/search/names?username={username}", User.class, username)
				.getId();

		restaurant.setOwnerId(userId);
		template.postForObject("http://localhost:8080/restaurants", restaurant, Restaurant.class);		
	}

	@Override
	public Restaurant getRestaurantByOwnerId(Long ownerId) {
		Restaurant restaurant = template.getForObject("http://localhost:8080/restaurants/search/owner?ownerId={ownerId}", Restaurant.class, ownerId);

		return restaurant;
	}

}
