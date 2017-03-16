package client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.PagedResources;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import pizzaOrder.client.service.implementation.UserServiceImpl;
import pizzaOrder.restService.model.nonActivatedUsers.NonActivatedUser;
import pizzaOrder.restService.model.users.User;
import pizzaOrder.security.SecurityService;
import pizzaOrder.security.UserSecurityService;

public class UserServiceTest {

	@Mock(name = "defaultTemplate")
	private RestTemplate defaultTemplate;

	@Mock
	private UserSecurityService userSecurityService;

	@Mock
	private SecurityService securityService;

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private UserServiceImpl userService;

	private NonActivatedUser testNonActivatedUser;

	private User testUser;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		testNonActivatedUser = new NonActivatedUser();
		testNonActivatedUser.setId(2L);
		testNonActivatedUser.setUsername("testUsername");
		testNonActivatedUser.setPassword("testPassword");
		testNonActivatedUser.setPhone(123456);
		testNonActivatedUser.setMail("test@mail");
		testNonActivatedUser.setRole("USER");

		testUser = new User();
		testUser.setId(2L);
		testUser.setUsername("testUsername");
		testUser.setPassword("testPassword");
		testUser.setPhone(123456);
		testUser.setMail("test@mail");
		testUser.setRole("USER");
	}

	@Test
	public void test_if_service_changes_user_id_after_posting_to_db() throws Exception {

		Mockito.when(defaultTemplate.postForLocation(Matchers.anyString(), Mockito.any(NonActivatedUser.class),
				Matchers.eq(NonActivatedUser.class))).thenReturn(new URI("aaa"));

		Mockito.when(defaultTemplate.getForObject(isA(URI.class), Matchers.eq(NonActivatedUser.class)))
				.thenReturn(testNonActivatedUser);

		NonActivatedUser user = new NonActivatedUser();
		userService.addNonActivatedUser(user);
		assertThat(user.getId(), is(2L));
	}

	// @Test
	// nie dziala
	// public void test_sending_activation_link() throws Exception{
	// userService.sendActivatingMail(testNonActivatedUser);
	//
	// verify(mailSender, times(1));
	// verifyNoMoreInteractions(mailSender);
	// }

	@Test
	public void authenticate_user() throws Exception {
		Mockito.when(defaultTemplate.getForObject(anyString(), Matchers.eq(User.class), anyLong()))
			   .thenReturn(testUser);

		userService.activateUser(testUser.getId());
		Assert.isNull(testUser.getId()); 				// Id should be null before posting in	User table

		verify(defaultTemplate, times(1)).getForObject(anyString(), Matchers.eq(User.class), anyLong());
		verify(defaultTemplate, times(1)).delete(anyString(), anyLong());
		verifyNoMoreInteractions(defaultTemplate);

		verify(userSecurityService, times(1)).save(Mockito.any(User.class));
		verifyNoMoreInteractions(userSecurityService);

		verify(securityService, times(1)).autologin(anyString(), anyString());
		verifyNoMoreInteractions(securityService);

	}
	
	@Test
	public void get_actual_user_id() throws Exception{
        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
        org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(testUser.getUsername(),testUser.getPassword(),grantedAuthorities);

	    Authentication auth = new UsernamePasswordAuthenticationToken(securityUser,null);	    
	    SecurityContextHolder.getContext().setAuthentication(auth);
	    
	    Mockito.when(defaultTemplate.getForObject(anyString(), Matchers.eq(User.class), anyLong())).thenReturn(testUser);
	    
	    userService.getActualUserId();
		verify(defaultTemplate, times(1)).getForObject(anyString(), Matchers.eq(User.class), anyString());

	}

}
