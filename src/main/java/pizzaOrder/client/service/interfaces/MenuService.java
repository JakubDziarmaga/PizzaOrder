package pizzaOrder.client.service.interfaces;

import java.net.URI;
import java.util.List;

import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.size.Size;

public interface MenuService {
	void checkIfMenuExists(Long idMenu);
	void checkIfMenuBelongsToRestaurant(Long idRestaurant, Long idMenu);
	String addMenu(Menu menu, Long idRestaurant);
	List<Menu> getMenuByRestaurantId(Long idRestaurant);
	public void getSizeByMenu(List<Menu>  menu);
	public void addSizeToMenu(List<Size> size, String menuURI);
}
