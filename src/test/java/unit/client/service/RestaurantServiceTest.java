package unit.client.service;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.client.exceptionHandler.RestaurantNotFoundException;
import pizzaOrder.client.service.implementation.RestaurantServiceImpl;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;


public class RestaurantServiceTest {

	@Mock(name = "halTemplate")
	private RestTemplate halTemplate;

	@Mock
	private RestaurantNotFoundException restaurantNotFoundException;

	@InjectMocks
	private RestaurantServiceImpl restaurantService;

	private Restaurant expectedRestaurant;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		expectedRestaurant = new Restaurant(1L, "first", "firstCity", "firstAddress", 12, 1L);
	}

	@Test
	public void get_all_restaurant_list() throws Exception {
		Restaurant first = new Restaurant(1L, "first", "firstCity", "firstAddress", 12, 1L);
		Restaurant second = new Restaurant(2L, "second", "secondcity", "secondAddress", 23, 2L);

		PagedResources<Restaurant> persistentEntityResource = new PagedResources<Restaurant>(
				Arrays.asList(first, second), new PageMetadata(1, 0, 10));

		Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(PagedResources.class)))
				.thenReturn(persistentEntityResource);

		List<Restaurant> restaurantList = restaurantService.getAllRestaurantsList();

		verify(halTemplate, times(1)).getForObject(anyString(), Matchers.eq(PagedResources.class));
		verifyNoMoreInteractions(halTemplate);
		Assert.notNull(restaurantList);
		assertThat(restaurantList, hasSize(2));
		assertThat(restaurantList, hasItem(first));
		assertThat(restaurantList, hasItem(second));
	}

	@Test
	public void get_restaurant_by_id() throws Exception {

		Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(Restaurant.class), anyLong()))
				.thenReturn(expectedRestaurant);

		Restaurant actualRestaurant = restaurantService.getRestaurantById(1L);

		verify(halTemplate, times(1)).getForObject(anyString(), eq(Restaurant.class), anyLong());
		verifyNoMoreInteractions(halTemplate);
		Assert.notNull(actualRestaurant);
		assertThat(actualRestaurant, is(expectedRestaurant));
	}

	@Test(expected = RestaurantNotFoundException.class)
	public void throw_RestaurantNotFoundException_when_restaurant_doesnt_exist() throws Exception {
		Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(Restaurant.class), anyLong())).thenThrow(HttpClientErrorException.class);

		restaurantService.getRestaurantById(1L);
	}

	@Test
	public void add_restaurant() throws Exception {
        set_up_authentication();

		User testUser = new User();
		testUser.setId(5L);
	    
		Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(User.class), Matchers.anyString())).thenReturn(testUser);

		restaurantService.addRestaurant(expectedRestaurant);
		
		verify(halTemplate, times(1)).getForObject(anyString(), eq(User.class), anyString());
		verify(halTemplate, times(1)).postForObject(anyString(), any(Restaurant.class), eq(Restaurant.class));
		verifyNoMoreInteractions(halTemplate);
		assertThat(expectedRestaurant.getOwnerId(), is(5L));

	}

	private void set_up_authentication() {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
        org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User("test","test",grantedAuthorities);

	    Authentication auth = new UsernamePasswordAuthenticationToken(securityUser,null);	    
	    SecurityContextHolder.getContext().setAuthentication(auth);
	}
	@Test
	public void get_restaurant_by_owner_id() throws Exception {

		Mockito.when(halTemplate.getForObject(Matchers.anyString(), Matchers.eq(Restaurant.class), Matchers.anyLong())).thenReturn(expectedRestaurant);

		Restaurant actualRestaurant = restaurantService.getRestaurantByOwnerId(1L);		
		
		verify(halTemplate, times(1)).getForObject(Matchers.anyString(), Matchers.eq(Restaurant.class), Matchers.anyLong());
		verifyNoMoreInteractions(halTemplate);
		assertThat(actualRestaurant, is(expectedRestaurant));
	}

}
