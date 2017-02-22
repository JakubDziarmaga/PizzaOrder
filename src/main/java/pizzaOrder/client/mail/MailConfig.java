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
		mailSender.setPort(587); // port poprawny tylko dla gmail
//		mailSender.setPort(465);
		mailSender.setUsername("pizza0rd3r");
		mailSender.setPassword("pizzaresttest");

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.starttls.enable", "true");
		javaMailProperties.put("mail.smtp.auth", "true");
//		javaMailProperties.put("mail.smtp.starttls.enable", "true");
//		javaMailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true); 
		javaMailProperties.put("mail.smtp.ssl.trust", "*");
		javaMailProperties.put("mail.smtp.ssl.socketFactory", sf);
		
//		MailSSLSocketFactory sf = new MailSSLSocketFactory();
//		sf.setTrustAllHosts(true); 
//		javaMailProperties.put("mail.imap.ssl.trust", "*");
//		javaMailProperties.put("mail.imap.ssl.socketFactory", sf);
//		
//		javaMailProperties.put("mail.transport.protocol", "smtps");
//		javaMailProperties.put("mail.smtps.host", "hostname");
//		javaMailProperties.put("mail.smtp.auth", "true");
//		javaMailProperties.put("mail.smtp.ssl.enable","true");
////		javaMailProperties.put("mail.smtps.ssl.checkserveridentity", "false");
////		javaMailProperties.put("mail.smtps.ssl.trust", "*");
//		javaMailProperties.put("mail.smtp.socketFactory.port",587);
//		javaMailProperties.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
//		javaMailProperties.put("mail.smtp.socketFactory.fallback", "true");
		mailSender.setJavaMailProperties(javaMailProperties);

		return mailSender;
	}
}
