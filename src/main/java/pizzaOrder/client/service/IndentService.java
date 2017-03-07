package pizzaOrder.client.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import pizzaOrder.restService.model.indent.Indent;

@Service
public interface IndentService {

	void checkIfIndentExists(Long idIndent);
	void payForIndent(Long idIndent);
	void checkIfActualUserIsOwnerOfIndent(Long idIndent);
	void deleteIndent(Long idIndent);
	void addIndents(Long idRestaurant, Long idMenu);
	List<Indent> getPayedIndentsByRestaurantId(Long restaurantId);
	List<Indent> getIndentsByUsername(String username);

}
