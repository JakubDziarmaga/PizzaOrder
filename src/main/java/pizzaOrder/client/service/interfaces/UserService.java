package pizzaOrder.client.service.interfaces;

import javax.mail.MessagingException;

import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;

public interface UserService {

	void addNonActivatedUser(NonActivatedUser user) throws MessagingException;
	void sendActivatingMail(NonActivatedUser user) throws MessagingException;
	void activateUser(Long nonActivatedUserId);
	Long getActualUserId();
}