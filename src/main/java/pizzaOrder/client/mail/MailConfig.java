package pizzaOrder.client.mail;

import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.MessagingException;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class MailConfig {

	/**
	 * Configure Gmail mailsender
	 */
	@Bean
	public JavaMailSender mailSender(Environment env) throws MessagingException, GeneralSecurityException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(465); 
		mailSender.setUsername("pizza0rd3r");
		mailSender.setPassword("smieja123");

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.auth", "true");
		javaMailProperties.put("mail.smtp.socketFactory.port", 465);
		javaMailProperties.put("mail.smtp.port", 465);
		javaMailProperties.put("mail.debug", "true");
		
		mailSender.setJavaMailProperties(javaMailProperties);

		return mailSender;
	}
}
