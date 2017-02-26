package pizzaOrder.client.exceptionHandler;

public class IndentAlreadyPaid extends RuntimeException{

	private Long indentId;

	public IndentAlreadyPaid(Long indentId) {
		super();
		this.indentId = indentId;
	}
	
}
