package pizzaOrder.restService.model.ingredients;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "ingredients",path = "ingredients")
public interface IngredientsRepository extends PagingAndSortingRepository<Ingredients, Long>{

	@RestResource(path = "asc")
	public List<Ingredients> findByOrderByNameAsc();
}
