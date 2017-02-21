package pizzaOrder.security;

import pizzaOrder.restService.model.users.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}