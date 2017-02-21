package pizzaOrder.restService.model.ingredients;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "ingredients",path = "ingredients")
public interface IngredientsRepository extends PagingAndSortingRepository<Ingredients, Long>{

	
}
