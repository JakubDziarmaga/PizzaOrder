package pizzaOrder.client.service.implementation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.exceptionHandler.IndentAlreadyPaid;
import pizzaOrder.client.exceptionHandler.IndentNotFoundException;
import pizzaOrder.client.exceptionHandler.NotPermittedException;
import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.cart.Cart;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.size.Size;
import pizzaOrder.restService.model.users.User;

@Service
public class IndentServiceImpl implements IndentService {

	@Autowired
	@Qualifier("halTemplate")
	private RestTemplate halTemplate;
	
	@Autowired
	@Qualifier("defaultTemplate")
	private RestTemplate defaultTemplate;
	
	@Autowired
	@Qualifier("halObjectMapper")
	private ObjectMapper mapper;
	
	@Autowired
	private RestaurantService restaurantService;
	
	@Autowired
	private MenuService menuService;
	
	/**
	 * Change indent.isPaid to true 
	 * @throw IndentAlreadyPaid if indent was already paid
	 */
	@Override
	public void payForIndent(Long idIndent) {
		checkIfIndentExists(idIndent);
		checkIfActualUserIsOwnerOfIndent(idIndent);
//		Indent indent = defaultTemplate.getForObject("http://localhost:8080/indents/{id}", Indent.class, idIndent);
		Indent indent = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{id}", Indent.class, idIndent);

		if (indent.isPaid())
			throw new IndentAlreadyPaid(idIndent);
		
		indent.setPaid(true);
//		defaultTemplate.put("http://localhost:8080/indents/{id}", indent, idIndent);
		defaultTemplate.put("https://pizzaindent.herokuapp.com/indents/{id}", indent, idIndent);

	}

	/**
	 * Check if indent with id = idIndent exists in DB
	 * @throw IndentNotFoundException if it doesn't
	 */
	@Override
	public void checkIfIndentExists(Long idIndent) {

		try {
//			defaultTemplate.getForObject("http://localhost:8080/indents/{idIndent}", Indent.class, idIndent);
			defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{idIndent}", Indent.class, idIndent);

		} catch(HttpClientErrorException e){
			throw new IndentNotFoundException(idIndent);
		}
		
	}
	
	/**
	 * Check if actual user has indent with id = idIndent in DB
	 * @throw IndentNotFoundException if he hasn't
	 */
	@Override
	public void checkIfActualUserIsOwnerOfIndent(Long idIndent) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

//		String userUrl = halTemplate.getForObject("http://localhost:8080/indents/{id}", PagedResources.class, idIndent)
//				.getLink("user").getHref();
		String userUrl = halTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{id}", PagedResources.class, idIndent)
				.getLink("user").getHref();
		if (!username.equals(halTemplate.getForObject(userUrl, User.class).getUsername()))
			throw new NotPermittedException();
	}
	
	/**
	 * Delete indent if it exists in db and if actual user has indent with id = idIndent in DB
	 */
	@Override
	public void deleteIndent(Long idIndent) {

		checkIfIndentExists(idIndent);
		checkIfActualUserIsOwnerOfIndent(idIndent);


		
//		Collection<?> cartHal = halTemplate.getForObject("http://localhost:8080/carts/search/indent?indentId={idIndent}", PagedResources.class,idIndent).getContent();
		Collection<?> cartHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/search/indent?indentId={idIndent}", PagedResources.class,idIndent).getContent();
		List<Cart> cartList = mapper.convertValue(cartHal, new TypeReference<List<Cart>>() {});
		
		for(Cart cart:cartList){
//			defaultTemplate.delete("http://localhost:8080/carts/{idIndent}", cart.getId());
			defaultTemplate.delete("https://pizzaindent.herokuapp.com/carts/{idIndent}", cart.getId());

		}
		
//		defaultTemplate.delete("http://localhost:8080/indents/{idIndent}", idIndent);
		defaultTemplate.delete("https://pizzaindent.herokuapp.com/indents/{idIndent}", idIndent);

		
//		halTemplate.delete("http://localhost:8080/indents/{idIndent}", idIndent);
		halTemplate.delete("https://pizzaindent.herokuapp.com/indents/{idIndent}", idIndent);

	}
	
	/**
	 * Post new indent
	 * Link it with actual user, menu and restaurant
	 */
	@Override
	public void addIndents(Long idRestaurant, Long idMenu, Long idSize) {
		
		restaurantService.getRestaurantById(idRestaurant);
		menuService.checkIfMenuExists(idMenu);
		menuService.checkIfMenuBelongsToRestaurant(idRestaurant, idMenu);
		
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		Long userId = defaultTemplate.getForObject("http://localhost:8080/users/search/names?username={username}",User.class, auth.getName()).getId();
		Long userId = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/users/search/names?username={username}",User.class, auth.getName()).getId();

		try{
//		Long idIndent = defaultTemplate.getForObject("http://localhost:8080/indents/search/indent?userId="+userId+"&restaurantId="+idRestaurant, Long.class);
		Long idIndent = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/search/indent?userId="+userId+"&restaurantId="+idRestaurant, Long.class);
		addNewMenuToIndent(idIndent, idMenu, idSize);
		return;

		}
		catch(HttpClientErrorException e){
			System.out.println(e.getStackTrace());
		}
		catch(HttpServerErrorException e){
			System.out.println(e.getStackTrace());
		}
		
//		Size size = defaultTemplate.getForObject("http://localhost:8080/size/{idSize}", Size.class,idSize);
		Size size = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/size/{idSize}", Size.class,idSize);

		Indent indent = new Indent();
		indent.setPrice(size.getPrice());
		indent.setDate(new Date());																				
//		URI newIndentURI = defaultTemplate.postForLocation("http://localhost:8080/indents/", indent);
		URI newIndentURI = defaultTemplate.postForLocation("https://pizzaindent.herokuapp.com/indents/", indent);

		
		//Posting restaurant to indent
//		HttpEntity<String> restaurantEntity = new HttpEntity<String>("http://localhost:8080/restaurants/" + idRestaurant, reqHeaders);
		HttpEntity<String> restaurantEntity = new HttpEntity<String>("https://pizzaindent.herokuapp.com/restaurants/" + idRestaurant, reqHeaders);

//		HttpEntity<String> restaurantEntity = new HttpEntity<String>("https://pizzaindent.herokuapp.com/restaurants/" + idRestaurant, reqHeaders);
		defaultTemplate.exchange(newIndentURI + "/restaurant", HttpMethod.PUT, restaurantEntity, String.class);

		//Posting cart to indent
		Cart cart = new Cart();
		cart.setAmount(1);
		cart.setPrice(size.getPrice());
//		URI newCartURI = defaultTemplate.postForLocation("http://localhost:8080/carts/", cart);
		URI newCartURI = defaultTemplate.postForLocation("https://pizzaindent.herokuapp.com/carts/", cart);

		HttpEntity<String> indentEntity = new HttpEntity<String>(newIndentURI.toString(), reqHeaders);
		defaultTemplate.exchange(newCartURI + "/indent", HttpMethod.PUT, indentEntity, String.class);

		//Posting size to cart
//		HttpEntity<String> sizeEntity = new HttpEntity<String>("http://localhost:8080/size/" + idSize, reqHeaders);
		HttpEntity<String> sizeEntity = new HttpEntity<String>("https://pizzaindent.herokuapp.com/size/" + idSize, reqHeaders);
		defaultTemplate.exchange(newCartURI + "/size", HttpMethod.PUT, sizeEntity, String.class);

		
		//Posting user to indent
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		Long userId = defaultTemplate.getForObject("http://localhost:8080/users/search/names?username={username}",User.class, auth.getName()).getId();
//		Long userId = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/users/search/names?username={username}",User.class, auth.getName()).getId();
//		HttpEntity<String> userEntity = new HttpEntity<String>("http://localhost:8080/users/" + userId, reqHeaders);
		HttpEntity<String> userEntity = new HttpEntity<String>("https://pizzaindent.herokuapp.com/" + userId, reqHeaders);
//		HttpEntity<String> userEntity = new HttpEntity<String>("https://pizzaindent.herokuapp.com/users/" + userId, reqHeaders);
		defaultTemplate.exchange(newIndentURI + "/user", HttpMethod.PUT, userEntity, String.class);

		//Posting menu to indent
//		HttpEntity<String> menuEntity = new HttpEntity<String>("http://localhost:8080/menu/" + idMenu, reqHeaders);
//		HttpEntity<String> menuEntity = new HttpEntity<String>("https://pizzaindent.herokuapp.com/menu/" + idMenu, reqHeaders);

//		defaultTemplate.exchange(newIndentURI + "/menu", HttpMethod.PUT, menuEntity, String.class);	
		
	}
	
	public void addNewMenuToIndent(Long idIndent, Long idMenu, Long idSize){
		
//		Indent indent = defaultTemplate.getForObject("http://localhost:8080/indents/{indentId}", Indent.class,idIndent);
		Indent indent = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{indentId}", Indent.class,idIndent);

		double indentPrice = indent.getPrice();
//		Collection<?> cartHal = halTemplate.getForObject("http://localhost:8080/indents/{indentId}/cart", PagedResources.class,idIndent).getContent();		
		Collection<?> cartHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{indentId}/cart", PagedResources.class,idIndent).getContent();		

		List<Cart> cartList = mapper.convertValue(cartHal, new TypeReference<List<Cart>>() {});
		
		for(Cart c:cartList){
//			Size size = defaultTemplate.getForObject("http://localhost:8080/carts/{idCart}/size", Size.class, c.getId());
			Size size = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCart}/size", Size.class, c.getId());

			if(size.getId() == idSize){
				int amount = c.getAmount();
				double price = size.getPrice();
				c.setAmount(amount+1);
				c.setPrice(price+size.getPrice());
//				defaultTemplate.put("http://localhost:8080/carts/{idCart}", c, c.getId());
				defaultTemplate.put("https://pizzaindent.herokuapp.com/carts/{idCart}", c, c.getId());

				indent.setPrice(indentPrice+size.getPrice());
//				defaultTemplate.put("http://localhost:8080/indents/{indentId}", indent, idIndent);
				defaultTemplate.put("https://pizzaindent.herokuapp.com/indents/{indentId}", indent, idIndent);

				return;
			}				
		}
		HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("text", "uri-list").toString());
		reqHeaders.add(HttpHeaders.CONTENT_TYPE, new MediaType("application", "json").toString());
		
//		Size size = defaultTemplate.getForObject("http://localhost:8080/size/{idSize}", Size.class, idSize);
		Size size = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/size/{idSize}", Size.class, idSize);

		double price = size.getPrice();
		
		Cart cart = new Cart();
		cart.setAmount(1);
		cart.setPrice(price);
//		URI newCartURI = defaultTemplate.postForLocation("http://localhost:8080/carts/", cart);
		URI newCartURI = defaultTemplate.postForLocation("https://pizzaindent.herokuapp.com/carts/", cart);

//		HttpEntity<String> sizeEntity = new HttpEntity<String>("http://localhost:8080/size/" + idSize, reqHeaders);
		HttpEntity<String> sizeEntity = new HttpEntity<String>("https://pizzaindent.herokuapp.com/size/" + idSize, reqHeaders);

		defaultTemplate.exchange(newCartURI+"/size", HttpMethod.PUT, sizeEntity, String.class);
		
//		HttpEntity<String> indentEntity = new HttpEntity<String>("http://localhost:8080/indents/" + idIndent, reqHeaders);
		HttpEntity<String> indentEntity = new HttpEntity<String>("https://pizzaindent.herokuapp.com/indents/" + idIndent, reqHeaders);

		defaultTemplate.exchange(newCartURI+"/indent", HttpMethod.PUT, indentEntity, String.class);
		
		indent.setPrice(indentPrice+size.getPrice());
//		defaultTemplate.put("http://localhost:8080/indents/{indentId}", indent, idIndent);
		defaultTemplate.put("https://pizzaindent.herokuapp.com/indents/{indentId}", indent, idIndent);

	}

	
	/**
	 * @return List of restaurant which are paid
	 */
	@Override
	public List<Indent> getPayedIndentsByRestaurantId(Long restaurantId) {
//		Collection<?> indentsHal =   halTemplate.getForObject("http://localhost:8080/restaurants/{restaurantId}/indent",PagedResources.class,restaurantId).getContent();
		Collection<?> indentsHal =   halTemplate.getForObject("https://pizzaindent.herokuapp.com/restaurants/{restaurantId}/indent",PagedResources.class,restaurantId).getContent();

		List<Indent> indents = mapper.convertValue(indentsHal, new TypeReference<List<Indent>>() {});
	
		List<Indent> payedIndents = new ArrayList<Indent>();
	
		for(Indent indent : indents){
			if(!indent.isPaid()) continue;
			
			//Get user entity
//			User user = halTemplate.getForObject("http://localhost:8080/indents/{id}/user", User.class,indent.getId());
			User user = halTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{id}/user", User.class,indent.getId());

//			Collection<?> cartHal = halTemplate.getForObject("http://localhost:8080/indents/{id}/cart",PagedResources.class,indent.getId()).getContent();
			Collection<?> cartHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{id}/cart",PagedResources.class,indent.getId()).getContent();

			List<Cart> cartList = mapper.convertValue(cartHal, new TypeReference<List<Cart> >() {});

			for(Cart c : cartList){
				
//				Size size = defaultTemplate.getForObject("http://localhost:8080/carts/{idCarts}/size", Size.class,c.getId());
				Size size = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCarts}/size", Size.class,c.getId());

//				Menu menu = defaultTemplate.getForObject("http://localhost:8080/size/{sizeId}/menu", Menu.class,size.getId());
				Menu menu = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/size/{sizeId}/menu", Menu.class,size.getId());

//				Collection<?> ingredientsHal = halTemplate.getForObject("http://localhost:8080/menu/{menuId}/ingredients", PagedResources.class,menu.getId()).getContent();		
				Collection<?> ingredientsHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/menu/{menuId}/ingredients", PagedResources.class,menu.getId()).getContent();		

				List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
				menu.setIngredients(ingredients);
				size.setMenu(menu);
				c.setSize(size);
			}
			indent.setCart(cartList);
			
			//Get menu entity
//			Menu menu = halTemplate.getForObject("http://localhost:8080/indents/{id}/menu", Menu.class,indent.getId());
//			Menu menu = halTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{id}/menu", Menu.class,indent.getId());

			//Get ingredients entity
//			Collection<?> ingredientsHal =halTemplate.getForObject("http://localhost:8080/menu/{id}/ingredients", PagedResources.class,menu.getId()).getContent();
//			Collection<?> ingredientsHal =halTemplate.getForObject("https://pizzaindent.herokuapp.com/menu/{id}/ingredients", PagedResources.class,menu.getId()).getContent();

//			List<Ingredients> tempIngredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients> >() {});
			
			//Link entities
//			menu.setIngredients(tempIngredients);
//			indent.setMenu(menu);
			indent.setUser(user);

			payedIndents.add(indent);
		}
		return payedIndents;
	}
	
	/**
	 * @return List of restaurant which user added to cart
	 */
	@Override
	public List<Indent> getIndentsByUsername(String username) {
//		String indentUrl = halTemplate.getForObject("http://localhost:8080/users/search/names?username={username}",PagedResources.class, username).getLink("indent").getHref();
		String indentUrl = halTemplate.getForObject("https://pizzaindent.herokuapp.com/users/search/names?username={username}",PagedResources.class, username).getLink("indent").getHref();

		Collection<?> indentsHal = halTemplate.getForObject(indentUrl, PagedResources.class).getContent();		
		List<Indent> indents = mapper.convertValue(indentsHal, new TypeReference<List<Indent>>() {});

		for(Indent indent : indents){
			//Get restaurant entity
//			Restaurant restaurant = halTemplate.getForObject("http://localhost:8080/indents/{indentId}/restaurant", Restaurant.class,indent.getId());
			Restaurant restaurant = halTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{indentId}/restaurant", Restaurant.class,indent.getId());
			
			
//			Collection<?> cartHal = halTemplate.getForObject("http://localhost:8080/indents/{indentId}/cart", PagedResources.class,indent.getId()).getContent();		
			Collection<?> cartHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{indentId}/cart", PagedResources.class,indent.getId()).getContent();		

			List<Cart> cartList = mapper.convertValue(cartHal, new TypeReference<List<Cart>>() {});
			
			
			for(Cart c : cartList){

//				Size size = defaultTemplate.getForObject("http://localhost:8080/carts/{idCarts}/size", Size.class,c.getId());
				Size size = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCarts}/size", Size.class,c.getId());

//				Menu menu = defaultTemplate.getForObject("http://localhost:8080/size/{sizeId}/menu", Menu.class,size.getId());
				Menu menu = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/size/{sizeId}/menu", Menu.class,size.getId());

//				Collection<?> ingredientsHal = halTemplate.getForObject("http://localhost:8080/menu/{menuId}/ingredients", PagedResources.class,menu.getId()).getContent();		
				Collection<?> ingredientsHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/menu/{menuId}/ingredients", PagedResources.class,menu.getId()).getContent();		

				List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
				menu.setIngredients(ingredients);
				size.setMenu(menu);
				c.setSize(size);
			}
			//Link entities
//			menu.setIngredients(ingredients);			
//			indent.setMenu(menu);
			indent.setRestaurant(restaurant);
			indent.setCart(cartList);
		}
		return indents;
	}
	
	public void deleteMenuFromCart(Long idIndent, Long idCart){
		
//		Cart cart = defaultTemplate.getForObject("http://localhost:8080/carts/{idCart}", Cart.class,idCart);
		Cart cart = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCart}", Cart.class,idCart);

//		defaultTemplate.delete("http://localhost:8080/carts/{idCart}", idCart);
		defaultTemplate.delete("https://pizzaindent.herokuapp.com/carts/{idCart}", idCart);

//		Collection<?> cartHal = halTemplate.getForObject("http://localhost:8080/indents/{idIndent}/cart", PagedResources.class,idIndent).getContent();	
		Collection<?> cartHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{idIndent}/cart", PagedResources.class,idIndent).getContent();		

		List<Cart> cartList = mapper.convertValue(cartHal, new TypeReference<List<Cart>>() {});
		
		if(cartList.isEmpty()){
			System.out.println("pusty koszyk");
//			defaultTemplate.delete("http://localhost:8080/indents/{idIndent}", idIndent);
			defaultTemplate.delete("https://pizzaindent.herokuapp.com/indents/{idIndent}", idIndent);

		}
		else{
//			Indent indent = defaultTemplate.getForObject("http://localhost:8080/indents/{idIndent}", Indent.class,idIndent);
			Indent indent = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{idIndent}", Indent.class,idIndent);

			double price = indent.getPrice();
			indent.setPrice(price-cart.getPrice());
//			defaultTemplate.put("http://localhost:8080/indents/{idIndent}", indent,idIndent);
			defaultTemplate.put("https://pizzaindent.herokuapp.com/indents/{idIndent}", indent,idIndent);

		}
		
	}


	@Override
	public Indent getIndentsByUsernameAndRestaurant(Long idRestaurant) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getPrincipal() == "anonymousUser") return null;

//		Long userId = defaultTemplate.getForObject("http://localhost:8080/users/search/names?username={username}",User.class, auth.getName()).getId();	
		Long userId = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/users/search/names?username={username}",User.class, auth.getName()).getId();	

		try{
//			Long idIndent = defaultTemplate.getForObject("http://localhost:8080/indents/search/indent?userId="+userId+"&restaurantId="+idRestaurant, Long.class);
			Long idIndent = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/search/indent?userId="+userId+"&restaurantId="+idRestaurant, Long.class);

//			Indent indent = defaultTemplate.getForObject("http://localhost:8080/indents/{indentId}", Indent.class,idIndent);
			Indent indent = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{indentId}", Indent.class,idIndent);

//			Collection<?> cartHal = halTemplate.getForObject("http://localhost:8080/indents/{indentId}/cart", PagedResources.class,idIndent).getContent();		
			Collection<?> cartHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/indents/{indentId}/cart", PagedResources.class,idIndent).getContent();		

			List<Cart> cartList = mapper.convertValue(cartHal, new TypeReference<List<Cart>>() {});
			
			for(Cart c : cartList){
	
//				Size size = defaultTemplate.getForObject("http://localhost:8080/carts/{idCarts}/size", Size.class,c.getId());
				Size size = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCarts}/size", Size.class,c.getId());

//				Menu menu = defaultTemplate.getForObject("http://localhost:8080/size/{sizeId}/menu", Menu.class,size.getId());
				Menu menu = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/size/{sizeId}/menu", Menu.class,size.getId());

//				Collection<?> ingredientsHal = halTemplate.getForObject("http://localhost:8080/menu/{menuId}/ingredients", PagedResources.class,menu.getId()).getContent();		
				Collection<?> ingredientsHal = halTemplate.getForObject("https://pizzaindent.herokuapp.com/menu/{menuId}/ingredients", PagedResources.class,menu.getId()).getContent();		

				List<Ingredients> ingredients = mapper.convertValue(ingredientsHal, new TypeReference<List<Ingredients>>() {});
				menu.setIngredients(ingredients);
				size.setMenu(menu);
				c.setSize(size);
			}
			indent.setCart(cartList);
			
			return indent;
		}
		catch(HttpClientErrorException e){
		return null;
		}
		catch(HttpServerErrorException e){
			return null;
		}
	}
	
	@Override
	public void incrementMenuInCart(Long idCart) {

//		Size size = defaultTemplate.getForObject("http://localhost:8080/carts/{idCart}/size", Size.class,idCart);
		Size size = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCart}/size", Size.class,idCart);

//		Cart cart = defaultTemplate.getForObject("http://localhost:8080/carts/{idCart}", Cart.class,idCart);
		Cart cart = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCart}", Cart.class,idCart);

//		Indent indent = defaultTemplate.getForObject("http://localhost:8080/carts/{idCart}/indent", Indent.class,idCart);
		Indent indent = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCart}/indent", Indent.class,idCart);

//		String indentUrl = halTemplate.getForObject("http://localhost:8080/carts/{idCart}/indent",PagedResources.class, idCart).getLink("indent").getHref();
		String indentUrl = halTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCart}/indent",PagedResources.class, idCart).getLink("indent").getHref();

		int amount = cart.getAmount();
		double price = cart.getPrice();
		
		cart.setAmount(amount+1);
		cart.setPrice(price + size.getPrice());		
//		defaultTemplate.put("http://localhost:8080/carts/{idCart}", cart, idCart);
		defaultTemplate.put("https://pizzaindent.herokuapp.com/carts/{idCart}", cart, idCart);

		double indentPrice = indent.getPrice();
		indent.setPrice(indentPrice + size.getPrice());
		defaultTemplate.put(indentUrl, indent);		
	}

	@Override
	public void decrementMenuInCart(Long idCart) {
//		Size size = defaultTemplate.getForObject("http://localhost:8080/carts/{idCart}/size", Size.class,idCart);
		Size size = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCart}/size", Size.class,idCart);

//		Cart cart = defaultTemplate.getForObject("http://localhost:8080/carts/{idCart}", Cart.class,idCart);
		Cart cart = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCart}", Cart.class,idCart);

//		Indent indent = defaultTemplate.getForObject("http://localhost:8080/carts/{idCart}/indent", Indent.class,idCart);
		Indent indent = defaultTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCart}/indent", Indent.class,idCart);

//		String indentUrl = halTemplate.getForObject("http://localhost:8080/carts/{idCart}/indent",PagedResources.class, idCart).getLink("indent").getHref();
		String indentUrl = halTemplate.getForObject("https://pizzaindent.herokuapp.com/carts/{idCart}/indent",PagedResources.class, idCart).getLink("indent").getHref();

		
		int amount = cart.getAmount();
		double price = cart.getPrice();

		if(amount>1){
			cart.setAmount(amount-1);
			cart.setPrice(price - size.getPrice());
//			defaultTemplate.put("http://localhost:8080/carts/{idCart}", cart, idCart);		
			defaultTemplate.put("https://pizzaindent.herokuapp.com/carts/{idCart}", cart, idCart);		

			double indentPrice = indent.getPrice();
			indent.setPrice(indentPrice - size.getPrice());
			defaultTemplate.put(indentUrl, indent);		

		}
		else{
			deleteMenuFromCart(indent.getId(),idCart);
		}
	}

	@Override
	public void changeIndentPrice(Double change) {
		// TODO Auto-generated method stub
		
	}
}
