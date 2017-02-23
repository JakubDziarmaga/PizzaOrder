package pizzaOrder.client.exceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ControllerExceptionHandler {
	protected final Logger log = LoggerFactory.getLogger(getClass());

//	@ResponseStatus(HttpStatus.NOT_FOUND)
//	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(RestaurantNotFoundException.class)
	public String restaurantNotFound(RedirectAttributes redirectAttributes){
		log.info("Restaurant not found.");
	    redirectAttributes.addFlashAttribute("error", "Restaurant not found.");
		return "redirect:/";

		
	}
}
