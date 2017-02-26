package pizzaOrder.client.exceptionHandler;

public class IndentNotFoundException extends RuntimeException {

	private final Long id;

	public IndentNotFoundException(Long id) {
		super();
		this.id = id;
	}
	
	
}
