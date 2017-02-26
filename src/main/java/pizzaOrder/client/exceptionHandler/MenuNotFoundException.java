package pizzaOrder.client.exceptionHandler;

public class MenuNotFoundException extends RuntimeException {

	private final Long id;

	public MenuNotFoundException(Long id) {
		super();
		this.id = id;
	}
	
	
}
