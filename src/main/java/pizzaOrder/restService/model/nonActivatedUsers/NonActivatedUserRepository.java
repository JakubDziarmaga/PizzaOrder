package pizzaOrder.restService.model.nonActivatedUsers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "nonactivatedusers",path = "nonactivatedusers")
public interface NonActivatedUserRepository extends JpaRepository<NonActivatedUser, Long> {

	@RestResource(path = "names")
	public NonActivatedUser findByUsername(@Param("username") String username);
}
