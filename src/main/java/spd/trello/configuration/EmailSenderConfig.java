package spd.trello.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailSenderConfig{
    @Value("${email.sentFromEmail}")
    String sentFromEmail;
    @Value("${email.sentFromPassword}")
    String sentFromPassword;
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl emailSender = new JavaMailSenderImpl();
        emailSender.setHost("smtp.gmail.com");
        emailSender.setPort(587);



        emailSender.setUsername(sentFromEmail);
        emailSender.setPassword(sentFromPassword);

        Properties props = emailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return emailSender;
    }

}
