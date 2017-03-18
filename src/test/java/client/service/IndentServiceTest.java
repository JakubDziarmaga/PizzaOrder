package client.service;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pizzaOrder.client.exceptionHandler.IndentNotFoundException;
import pizzaOrder.client.exceptionHandler.NotPermittedException;
import pizzaOrder.client.service.implementation.IndentServiceImpl;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

public class IndentServiceTest {

	@Mock(name = "defaultTemplate")
	private RestTemplate defaultTemplate;

	@Mock(name = "halTemplate")
	private RestTemplate halTemplate;

	@Spy
	private ObjectMapper mapper;

	@Mock	
	private RestaurantService restaurantService;
	
	@Mock
	private MenuService menuService;
	
    @InjectMocks
	private IndentServiceImpl indentService;
    
    
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
//    @Test
//    public void pay_for_indent() throws Exception{
//    	Indent indent = new Indent(1L, true, new User(), new Restaurant(), new Menu(), new Date());
//
//    	Mockito.when(defaultTemplate.getForObject(
//                Mockito.anyString(),
//                eq(Indent.class),
//                Matchers.anyLong()               
//                ))
//                .thenReturn(indent);
////    	Mockito.when(indentService.checkIfIndentExists(anyLong())).thenReturn(null);
//    	
//    	indentService.payForIndent(1L);
//    }
	@Test(expected = IndentNotFoundException.class)
	public void throw_IndentNotFoundException_when_indent_isnt_in_db() throws Exception{
		Mockito.when(defaultTemplate.getForObject(Matchers.anyString(), Matchers.eq(Indent.class), anyLong())).thenThrow(HttpClientErrorException.class);

		indentService.checkIfIndentExists(1L);
	}
	@Test
	public void check_if_actualUser_is_owner_of_indent() throws Exception{
	    setUpAuthentication();
	    
    	PagedResources<Indent> indentHal = new PagedResources<Indent>(Collections.emptyList(),  new PageMetadata(1, 0, 10));
    	indentHal.add(new Link("userUrl","user"));    	
   	    
	    
		Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class), anyLong())).thenReturn(indentHal);
		User user = new User();
		user.setUsername("testUser");
		Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(User.class))).thenReturn(user);
		
		indentService.checkIfActualUserIsOwnerOfIndent(1L);
	}
	

	private void setUpAuthentication() {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
	    grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
	    org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User("testUser","testPassword",grantedAuthorities);

	    Authentication auth = new UsernamePasswordAuthenticationToken(securityUser,null);	    
	    SecurityContextHolder.getContext().setAuthentication(auth);
	}
	
	@Test(expected = NotPermittedException.class)
	public void throw_NotPermittedException_when_actualUser_isnt_owner_of_indent() throws Exception{
	    setUpAuthentication();
	    
    	PagedResources<Indent> restaurantHal = new PagedResources<Indent>(Collections.emptyList(),  new PageMetadata(1, 0, 10));
    	restaurantHal.add(new Link("userUrl","user"));    	
   	    
	    
		Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class), anyLong())).thenReturn(restaurantHal);
		
		User differentUser = new User();
		differentUser.setUsername("differentUser");
		Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(User.class))).thenReturn(differentUser);
		indentService.checkIfActualUserIsOwnerOfIndent(1L);
	}
	
//	@Test
//	public void delete_indent() throws Exception{
//		
//		indentService.deleteIndent(1L);
//
//	}
	
	@Test
	public void add_indent() throws Exception{
	    setUpAuthentication();
	    User user = new User();
	    user.setId(1L);
		Mockito.when(defaultTemplate.getForObject(Matchers.anyString(), Matchers.eq(User.class),Matchers.anyString())).thenReturn(user);

		indentService.addIndents(1L, 1L);
		
		verify(defaultTemplate, times(1)).postForLocation(Matchers.anyString(),Matchers.any(Indent.class));
		verify(defaultTemplate, times(1)).getForObject(Matchers.anyString(),Matchers.eq(User.class),Matchers.anyString());
		verify(defaultTemplate, times(3)).exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.<HttpEntity<?>> any(), Mockito.<Class<?>> any());
		verifyNoMoreInteractions(defaultTemplate);
	}
	
	@Test
	public void get_indents_by_restaurant_id() throws Exception{
		Indent firstIndent = new Indent(1L, true, new User(), new Restaurant(), new Menu(), new Date());
		Indent secondIndent = new Indent(2L, false, new User(), new Restaurant(), new Menu(), new Date());
		Indent thirdIndent = new Indent(3L, true, new User(), new Restaurant(), new Menu(), new Date());

		User firstUser = new User(4L, "firstUsername", null, null, 0 , null);
		User secondUser = new User(5L, "firstUsername", null, null, 0 , null);
		User thirdUser = new User(6L, "firstUsername", null, null, 0 , null);

		Menu firstMenu = new Menu(7L, 10.0, null, null, null);
		Menu secondMenu = new Menu(8L, 20.0, null, null, null);
		Menu thirdMenu = new Menu(9L, 30.0, null, null, null);
		
    	PagedResources<Indent> indentsHal = new PagedResources<Indent>(Arrays.asList(firstIndent,secondIndent,thirdIndent),  new PageMetadata(1, 0, 10));
    	
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("indent"), Matchers.eq(PagedResources.class),anyLong())).thenReturn(indentsHal);
		
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("user"), Matchers.eq(User.class),Matchers.eq(1L))).thenReturn(firstUser);
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("menu"), Matchers.eq(Menu.class),Matchers.eq(1L))).thenReturn(firstMenu);
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("user"), Matchers.eq(User.class),Matchers.eq(2L))).thenReturn(secondUser);
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("menu"), Matchers.eq(Menu.class),Matchers.eq(2L))).thenReturn(secondMenu);
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("user"), Matchers.eq(User.class),Matchers.eq(3L))).thenReturn(thirdUser);
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("menu"), Matchers.eq(Menu.class),Matchers.eq(3L))).thenReturn(thirdMenu);
		
		Ingredients firstIngredient = new Ingredients(10L, "firstIngredientName");
		Ingredients secondIngredient = new Ingredients(11L, "secondIngredientName");
		Ingredients thirdIngredient = new Ingredients(12L, "thirdIngredientName");

    	PagedResources<Ingredients> ingredientsHal = new PagedResources<Ingredients>(Arrays.asList(firstIngredient,secondIngredient,thirdIngredient), new PageMetadata(1, 0, 10));
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("ingredients"), Matchers.eq(PagedResources.class),anyLong())).thenReturn(ingredientsHal);

		List<Indent> payedIndents = indentService.getPayedIndentsByRestaurantId(1L);
		
		verify(halTemplate, times(3)).getForObject(Matchers.anyString(),Matchers.eq(PagedResources.class),anyLong());
		verify(halTemplate, times(2)).getForObject(Matchers.anyString(),Matchers.eq(User.class),anyLong());
		verify(halTemplate, times(2)).getForObject(Matchers.anyString(),Matchers.eq(Menu.class),anyLong());
		verifyNoMoreInteractions(halTemplate);
		verifyNoMoreInteractions(defaultTemplate);

		Assert.assertNotNull(payedIndents);
		
		Assert.assertEquals(2, payedIndents.size());
		Assert.assertEquals(payedIndents.get(0).getMenu().getPrice(),firstMenu.getPrice());		
		Assert.assertEquals(payedIndents.get(0).getId(),firstIndent.getId());		
		Assert.assertEquals(payedIndents.get(0).getUser().getUsername(),firstUser.getUsername());		

		Assert.assertEquals(payedIndents.get(1).getMenu().getPrice(),thirdMenu.getPrice());		
		Assert.assertEquals(payedIndents.get(1).getId(),thirdIndent.getId());		
		Assert.assertEquals(payedIndents.get(1).getUser().getUsername(),thirdUser.getUsername());	

	}
	@Test
	public void get_indents_by_username() throws Exception{
		Indent firstIndent = new Indent(1L, true, new User(), new Restaurant(), new Menu(), new Date());
		Indent secondIndent = new Indent(2L, false, new User(), new Restaurant(), new Menu(), new Date());
		Indent thirdIndent = new Indent(3L, true, new User(), new Restaurant(), new Menu(), new Date());

		Restaurant firstRestaurant = new Restaurant(4L, "firstRestaurantName", null, null, 0, null);
		Restaurant secondRestaurant = new Restaurant(5L, "secondRestaurantName", null, null, 0, null);
		Restaurant thirdRestaurant = new Restaurant(6L, "thirdRestaurantName", null, null, 0, null);

		Menu firstMenu = new Menu(7L, 10.0, null, null, null);
		Menu secondMenu = new Menu(8L, 20.0, null, null, null);
		Menu thirdMenu = new Menu(9L, 30.0, null, null, null);
		
    	PagedResources<Indent> userHal = new PagedResources<Indent>(Collections.emptyList(),  new PageMetadata(1, 0, 10));    	
    	userHal.add(new Link("indentUrl","indent"));    	
		Mockito.when(halTemplate.getForObject(Matchers.startsWith("http://localhost:8080/users/"), Matchers.eq(PagedResources.class),anyString())).thenReturn(userHal);
		
    	PagedResources<Indent> indentsHal = new PagedResources<Indent>(Arrays.asList(firstIndent,secondIndent,thirdIndent),  new PageMetadata(1, 0, 10));    	
		Mockito.when(halTemplate.getForObject(Matchers.eq("indentUrl"), Matchers.eq(PagedResources.class))).thenReturn(indentsHal);
		
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("restaurant"), Matchers.eq(Restaurant.class),Matchers.eq(1L))).thenReturn(firstRestaurant);
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("restaurant"), Matchers.eq(Restaurant.class),Matchers.eq(2L))).thenReturn(secondRestaurant);
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("restaurant"), Matchers.eq(Restaurant.class),Matchers.eq(3L))).thenReturn(thirdRestaurant);

		Mockito.when(halTemplate.getForObject(Matchers.endsWith("menu"), Matchers.eq(Menu.class),Matchers.eq(1L))).thenReturn(firstMenu);
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("menu"), Matchers.eq(Menu.class),Matchers.eq(2L))).thenReturn(secondMenu);
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("menu"), Matchers.eq(Menu.class),Matchers.eq(3L))).thenReturn(thirdMenu);
		
		Ingredients firstIngredient = new Ingredients(10L, "firstIngredientName");
		Ingredients secondIngredient = new Ingredients(11L, "secondIngredientName");
		Ingredients thirdIngredient = new Ingredients(12L, "thirdIngredientName");

    	PagedResources<Ingredients> ingredientsHal = new PagedResources<Ingredients>(Arrays.asList(firstIngredient,secondIngredient,thirdIngredient), new PageMetadata(1, 0, 10));
		Mockito.when(halTemplate.getForObject(Matchers.endsWith("ingredients"), Matchers.eq(PagedResources.class),anyLong())).thenReturn(ingredientsHal);

		List<Indent> indentsList = indentService.getIndentsByUsername("testUsername");
		
		verify(halTemplate, times(4)).getForObject(Matchers.anyString(),Matchers.eq(PagedResources.class),anyString());
		verify(halTemplate, times(1)).getForObject(Matchers.anyString(),Matchers.eq(PagedResources.class));
		verify(halTemplate, times(3)).getForObject(Matchers.anyString(),Matchers.eq(Restaurant.class),anyLong());
		verify(halTemplate, times(3)).getForObject(Matchers.anyString(),Matchers.eq(Menu.class),anyLong());
		verifyNoMoreInteractions(halTemplate);
		verifyNoMoreInteractions(defaultTemplate);

		Assert.assertNotNull(indentsList);
		
		Assert.assertEquals(3, indentsList.size());
		Assert.assertEquals(indentsList.get(0).getMenu().getPrice(),firstMenu.getPrice());		
		Assert.assertEquals(indentsList.get(0).getId(),firstIndent.getId());		
		Assert.assertEquals(indentsList.get(0).getRestaurant().getName(),firstRestaurant.getName());		

		Assert.assertEquals(indentsList.get(1).getMenu().getPrice(),secondMenu.getPrice());		
		Assert.assertEquals(indentsList.get(1).getId(),secondIndent.getId());		
		Assert.assertEquals(indentsList.get(1).getRestaurant().getName(),secondRestaurant.getName());		
		
		Assert.assertEquals(indentsList.get(2).getMenu().getPrice(),thirdMenu.getPrice());		
		Assert.assertEquals(indentsList.get(2).getId(),thirdIndent.getId());		
		Assert.assertEquals(indentsList.get(2).getRestaurant().getName(),thirdRestaurant.getName());		

	}
}
