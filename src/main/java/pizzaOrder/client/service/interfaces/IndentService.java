package pizzaOrder.client.service.interfaces;

import java.util.List;

import org.springframework.stereotype.Service;

import pizzaOrder.restService.model.indent.Indent;

@Service
public interface IndentService {

	void checkIfIndentExists(Long idIndent);
	void payForIndent(Long idIndent);
	void checkIfActualUserIsOwnerOfIndent(Long idIndent);
	void deleteIndent(Long idIndent);
	void addIndents(Long idRestaurant, Long idMenu, Long idSize);
	List<Indent> getPayedIndentsByRestaurantId(Long restaurantId);
	List<Indent> getIndentsByUsername(String username);
	Indent getIndentsByUsernameAndRestaurant(Long idRestaurant);
	void addNewMenuToIndent(Long idIndent, Long idMenu, Long idSize);
	void deleteMenuFromCart(Long idIndents, Long idCart);
	void incrementMenuInCart(Long idCart);
	void decrementMenuInCart(Long idCart);
	void changeIndentPrice(Double change);
}
