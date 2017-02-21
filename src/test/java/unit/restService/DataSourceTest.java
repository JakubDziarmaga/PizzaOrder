package unit.restService;

import static org.junit.Assert.*;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import pizzaOrder.Application;
import pizzaOrder.restService.database.DataSourceConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = DataSourceConfiguration.class)
@ContextConfiguration(classes = { Application.class })

public class DataSourceTest {

	@Autowired 
	DataSource dataSource;
	
	@Mock
	DataSource mockDataSource;
	
	@Test
	public void connectToDataSource() throws SQLException{
		assertTrue(dataSource.getConnection().getAutoCommit());

	}
	
}
