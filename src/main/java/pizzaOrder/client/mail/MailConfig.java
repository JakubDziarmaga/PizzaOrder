package pizzaOrder.client.mail;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class MailConfig {

		@Bean
		public JavaMailSender getJavaMailSender() {
		    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		    mailSender.setHost("smtp.gmail.com");
		    mailSender.setPort(587);
		     
		    mailSender.setUsername("pizza0rd3r");
		    mailSender.setPassword("smieja123");
		     
		    Properties props = new Properties();
		    props.put("mail.transport.protocol", "smtp");
		    props.put("mail.smtp.auth", "true");
		    props.put("mail.smtp.starttls.enable", "true");
		    props.put("mail.debug", "true");
		    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		    mailSender.setJavaMailProperties(props);
	 
		    return mailSender;
		}

}

