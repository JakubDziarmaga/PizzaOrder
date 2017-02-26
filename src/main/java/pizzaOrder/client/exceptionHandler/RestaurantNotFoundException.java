package pizzaOrder.client.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Order")
public class RestaurantNotFoundException extends RuntimeException {

	private final Long id;

	public RestaurantNotFoundException(Long id) {
		super();
		this.id = id;
	}
	
	
}
