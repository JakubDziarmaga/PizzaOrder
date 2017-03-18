package pizzaOrder.restService.model.menu;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "menu",path = "menu")

public interface MenuRepository extends PagingAndSortingRepository<Menu,Long>{

}
