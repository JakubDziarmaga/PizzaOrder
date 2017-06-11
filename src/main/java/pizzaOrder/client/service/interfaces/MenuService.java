package pizzaOrder.client.service.interfaces;

import java.util.List;

import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.size.Size;

public interface MenuService {
	void checkIfMenuExists(Long idMenu);
	void checkIfMenuBelongsToRestaurant(Long idRestaurant, Long idMenu);
	void addMenu(Menu menu, Long idRestaurant);
	List<Menu> getMenuByRestaurantId(Long idRestaurant);
	public void getSizeByMenu(List<Menu>  menu);

}
