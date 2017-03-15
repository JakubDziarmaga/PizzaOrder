package pizzaOrder.client.service.interfaces;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import pizzaOrder.restService.model.menu.Menu;

public interface MenuService {
	void checkIfMenuExists(Long idMenu);
	void checkIfMenuBelongsToRestaurant(Long idRestaurant, Long idMenu);
	void addMenu(Menu menu, Long idRestaurant);
	List<Menu> getMenuByRestaurantId(Long idRestaurant);
}