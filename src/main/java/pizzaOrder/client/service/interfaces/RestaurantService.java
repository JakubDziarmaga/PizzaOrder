package pizzaOrder.client.service.interfaces;

import java.util.List;

import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.size.Size;
import pizzaOrder.restService.model.stars.Stars;

public interface RestaurantService {

	List<Restaurant> getAllRestaurantsList();
	Restaurant getRestaurantById(Long restaurantId);
	Restaurant getRestaurantByOwnerId(Long ownerId);
//	void checkIfRestaurantExists(Long restaurantId);
	void addRestaurant(Restaurant restaurant);
	List<Restaurant> getRestaurantsByCity(String city);
	Stars getStarsByRestaurantId(Long idRestaurant);
	void addStar(Long restaurantId, int rating);
}
