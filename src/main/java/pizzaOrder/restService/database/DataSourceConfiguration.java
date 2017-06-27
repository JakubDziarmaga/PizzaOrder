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
//		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//		dataSource.setUrl("jdbc:mysql://localhost:3306/sys?useLegacyDatetimeCode=false&serverTimezone=UTC");		
//		dataSource.setUsername("root");
//		dataSource.setPassword("smieja123");
		
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://ec2-54-227-237-223.compute-1.amazonaws.com:5432/d7n32in7ipk62h?sslmode=require");		
		dataSource.setUsername("zkhmuezlaztgpk");
		dataSource.setPassword("5da64a63148284176a523b752b197379ecc98e6683443caa4de1192ed61eff35");
		return dataSource;
	}
}