package pizzaOrder.restService.model.stars;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import pizzaOrder.restService.model.restaurant.Restaurant;

@RepositoryRestResource(collectionResourceRel = "stars",path = "stars")
public interface StarsRepository extends JpaRepository<Stars,Long> {
	
}
