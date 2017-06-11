package pizzaOrder.restService.model.size;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import pizzaOrder.restService.model.menu.Menu;

@RepositoryRestResource(collectionResourceRel = "size",path = "size")
public interface SizeRepository extends JpaRepository<Size,Long>{

}