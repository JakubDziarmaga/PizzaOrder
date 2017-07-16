package pizzaOrder.restService.model.indent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "indents",path = "indents")
public interface IndentRepository extends JpaRepository<Indent, Long> {
	
	@RestResource(path = "username")
	@Query("SELECT i.id FROM Indent i WHERE i.user.id = ?1")
	public Long findIndentIdByUserId(@Param("userId") Long userId);
	
	@RestResource(path = "indent")
	@Query("SELECT i.id FROM Indent i where i.user.id=?1 and i.restaurant.id = ?2 and isPaid=false")
	public Long findIndentIdByUserIdAndRestaurantId(@Param("userId") Long userId,@Param("restaurantId") Long restaurantId);
	

}
