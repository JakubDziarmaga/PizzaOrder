package pizzaOrder.client.service;

import java.util.List;

import pizzaOrder.restService.model.restaurant.Restaurant;

public interface RestaurantService {

	List<Restaurant> getAllRestaurantsList();
	Restaurant getRestaurantById(Long restaurantId);
	Restaurant getRestaurantByOwnerId(Long ownerId);
	void checkIfRestaurantExists(Long restaurantId);
	void addRestaurant(Restaurant restaurant);
}
