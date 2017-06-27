package pizzaOrder.restService.model.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.users.User;

@RepositoryRestResource(collectionResourceRel = "carts",path = "carts")
public interface CartRepository extends JpaRepository<Cart, Long> {

	
	@RestResource(path = "indent")
	public List<Cart> findByIndentId(@Param("indentId") Long indentId);
}
