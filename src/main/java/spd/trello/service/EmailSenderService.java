package spd.trello.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    private final JavaMailSender emailSender;

    @Value("${email.sentFromEmail}")
    String sentEmailFrom;

    public EmailSenderService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public  void sendMail(String sentEmailTo,
                          String subject,
                          String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sentEmailFrom);
        message.setTo(sentEmailTo);
        message.setText(body);
        message.setSubject(subject);

        emailSender.send(message);


    }
}
