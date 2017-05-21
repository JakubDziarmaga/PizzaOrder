package pizzaOrder.restService.model.restaurant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "restaurants",path = "restaurants")
public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

	@RestResource(path = "owner")
	public Restaurant findByOwnerId(@Param("ownerId") Long ownerId);
	
	@RestResource(path = "city")
	public List<Restaurant> findByCity(@Param("city") String city);
	
}
