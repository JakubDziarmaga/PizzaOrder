package client.service;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;

import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import pizzaOrder.Application;
import pizzaOrder.client.service.implementation.IndentServiceImpl;
import pizzaOrder.client.service.interfaces.IndentService;
import pizzaOrder.client.service.interfaces.MenuService;
import pizzaOrder.client.service.interfaces.RestaurantService;
import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)

public class IndentServiceTest {

	@MockBean(name = "defaultTemplate")
	private RestTemplate defaultTemplate;

	@MockBean(name = "halTemplate")
	private RestTemplate halTemplate;
	
	@MockBean	
	private RestaurantService restaurantService;
	
	@MockBean
	private MenuService menuService;
	
    @InjectMocks
	private IndentService indentService = new IndentServiceImpl();

    @Test
    public void pay_for_indent() throws Exception{
    	Indent indent = new Indent(1L, true, new User(), new Restaurant(), new Menu(), new Date());

    	Mockito.when(defaultTemplate.getForObject(
                Mockito.anyString(),
                eq(Indent.class),
                Matchers.anyLong()               
                ))
                .thenReturn(indent);
//    	Mockito.when(indentService.checkIfIndentExists(anyLong())).thenReturn(null);
    	
    	indentService.payForIndent(1L);
    }
	
}
