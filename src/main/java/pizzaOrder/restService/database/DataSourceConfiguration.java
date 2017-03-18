package pizzaOrder.restService.database;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
@Configuration		
public class DataSourceConfiguration {

	/**
	 * Configure mySQL DataSource
	 */
	@Bean 
	public DataSource dataSource(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/sys");		
		dataSource.setUsername("root");
		dataSource.setPassword("smieja123");
		return dataSource;
	}
}