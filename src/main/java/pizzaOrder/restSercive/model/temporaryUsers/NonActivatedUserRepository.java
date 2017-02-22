package pizzaOrder.restSercive.model.temporaryUsers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import pizzaOrder.restService.model.users.User;

@RepositoryRestResource(collectionResourceRel = "nonactivatedusers",path = "nonactivatedusers")
public interface NonActivatedUserRepository extends JpaRepository<NonActivatedUser, Long> {

	@RestResource(path = "names")
	public User findByUsername(@Param("username") String username);
}
