package pizzaOrder.client.service.interfaces;


import javax.mail.MessagingException;

import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.users.User;

public interface UserService {

	void addNonActivatedUser(NonActivatedUser user) throws MessagingException ;
	void sendActivatingMail(NonActivatedUser user) throws MessagingException;
	void activateUser(Long nonActivatedUserId);
	Long getActualUserId();
	User getUserByUsername(String username);
	boolean changePassword(String oldPassword, String newPassword);
	void changeMail(String newMail);
	Integer getAmountOfUnpayedIndents();
}