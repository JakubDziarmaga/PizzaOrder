package pizzaOrder.security;

import pizzaOrder.restService.model.users.User;

public interface UserSecurityService {
    void save(User user);

    User findByUsername(String username);
}