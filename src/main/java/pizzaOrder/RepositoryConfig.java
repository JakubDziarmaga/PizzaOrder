package pizzaOrder;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.size.Size;
import pizzaOrder.restService.model.stars.Stars;
import pizzaOrder.restService.model.users.User;

@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {
	
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.exposeIdsFor(Menu.class,Restaurant.class,User.class,Ingredients.class,Indent.class,
				Stars.class,NonActivatedUser.class,Size.class);		//Enable to send id in JSON
        
	}
}
