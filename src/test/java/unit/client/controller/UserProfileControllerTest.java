package unit.client.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pizzaOrder.Application;
import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.menu.Menu;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = Application.class)
//@WebAppConfiguration
//public class UserProfileControllerTest {
//
//	private MockMvc mockMvc;
//
//	@MockBean	
//	private IndentService indentServiceMock;
//
//	@Autowired
//	private WebApplicationContext webApplicationContext;
//
//	@Autowired
//	private FilterChainProxy springSecurityFilter;
//
//	private List<Indent> indentList;
//	
//	@Before
//	public void setUp() {
//		MockitoAnnotations.initMocks(this);
//		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(this.springSecurityFilter, "/*").build();
//		
//		set_up_authentication();
//
//	}
//
//	private void set_up_authentication() {
//		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
//		grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
//		org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User("test", "test", grantedAuthorities);
//
//		Authentication auth = new UsernamePasswordAuthenticationToken(securityUser, null);
//		SecurityContextHolder.getContext().setAuthentication(auth);
//	}
//
//	@Test
//	public void show_user_profile_page() throws Exception {
//
//		create_indentList();
//
//
//		Mockito.when(indentServiceMock.getIndentsByUsername(Matchers.any(String.class))).thenReturn(indentList);
//		
//		
//		mockMvc.perform(get("/user")
//			   .with(user("test").password("test").roles("USER")))
//			   .andDo(print()).andExpect(status().isOk())
//			   .andExpect(view().name("user"))
//			   .andExpect(model().attributeExists("actualUser"))
//			   .andExpect(model().attributeExists("indents"))
//			   .andExpect(model().attribute("indents",is(indentList) ));
//
//		verify(indentServiceMock, times(1)).getIndentsByUsername(Matchers.anyString());
//		verifyNoMoreInteractions(indentServiceMock);
//	}
//	
//	private void create_indentList() {
//		Indent firstIndent = new Indent();
//		Menu firstmenu = new Menu();
//		firstIndent.setMenu(firstmenu);
//		
//		Indent secondIndent = new Indent();
//		Menu secondmenu = new Menu();
//		secondIndent.setMenu(secondmenu);
//
//		indentList = new ArrayList<Indent> ();
//		indentList.add(firstIndent);
//		indentList.add(secondIndent);
//	}
//}
