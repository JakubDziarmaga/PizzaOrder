package pizzaOrder.restService.model.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "users",path = "users")
public interface UserRepository extends JpaRepository<User, Long> {

	@RestResource(path = "names")
	public User findByUsername(@Param("username") String username);
}
