package pizzaOrder.client.mail;

import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.MessagingException;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import com.sun.mail.util.MailSSLSocketFactory;

@Component
public class MailConfig {

	@Bean
	public JavaMailSender mailSender(Environment env) throws MessagingException, GeneralSecurityException {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587); 
		mailSender.setUsername("pizza0rd3r");
		mailSender.setPassword("pizzaresttest");

		Properties javaMailProperties = new Properties();
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true); 
		javaMailProperties.put("mail.smtp.starttls.enable", "true");
		javaMailProperties.put("mail.smtp.auth", "true");
		
		javaMailProperties.put("mail.smtp.ssl.trust", "*");
		javaMailProperties.put("mail.smtp.ssl.socketFactory", sf);
		
		mailSender.setJavaMailProperties(javaMailProperties);

		return mailSender;
	}
}
