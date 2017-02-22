package pizzaOrder;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import pizzaOrder.restSercive.model.temporaryUsers.NonActivatedUser;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {
	//TODO SPRAWDZ CZY TO JEST POTRZEBNE SKORO JEST W APPLICATION
	
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.exposeIdsFor(Menu.class,Restaurant.class,User.class,Ingredients.class,Indent.class,NonActivatedUser.class);//Enable to pass id in JSON
        
	}
}
