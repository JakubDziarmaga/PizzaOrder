package pizzaOrder.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Autowired
    private UserDetailsService userDetailsService;
    

    /**
     * Configure access to each page
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/users/**", "/registration","/restaurants/**","/menu/**", "/ingredients/**","/indents/**","/role/**","/nonactivatedusers/**" //rest url's
                    		,"/activate/**","/restaurant/**","/city","/stars/**","/size/**","/carts/**").permitAll()
                    .antMatchers("/css/**","/img/**","/fontello/**").permitAll()
                    .antMatchers("/restaurantOwner/**","/addRestaurant").hasRole("RESTAURANT_OWNER")
                    .antMatchers("/indent/**","/user","/addindents/**","/indent/delete/**","/indent/pay/**","/restaurant/*/score").hasRole("USER")                 
                    .anyRequest().authenticated()
                    .and().exceptionHandling().accessDeniedPage("/")
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()                    
                    .and()
                .logout()
                    .permitAll()
                    .logoutSuccessUrl("/")
                    .and() 
                .csrf().disable(); 
                
    }
    @Bean
	public UserDetailsService userDetailsService() {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(User.withUsername("admin").password("$2a$04$abYcGv5sTVTYpBHCgX00Euel/muSsXKMA7o20czspZOO7jdXXoJwG").roles("ACTUATOR").build());
		return manager;
	}
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}