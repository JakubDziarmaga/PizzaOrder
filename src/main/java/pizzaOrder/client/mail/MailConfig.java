package pizzaOrder.client.mail;

import java.util.Properties;

import javax.mail.MessagingException;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class MailConfig {

	@Bean
	public JavaMailSender mailSender(Environment env) throws MessagingException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587); // port poprawny tylko dla gmail
		mailSender.setUsername("pizza0rd3r");
		mailSender.setPassword("pizzatest123");

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.starttls.enable", "true");
		javaMailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		mailSender.setJavaMailProperties(javaMailProperties);

		return mailSender;
	}
}
