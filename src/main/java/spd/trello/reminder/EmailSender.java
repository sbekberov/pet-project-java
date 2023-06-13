package spd.trello.reminder;

import lombok.Data;
import org.springframework.stereotype.Component;
import spd.trello.service.EmailSenderService;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Data
public class EmailSender implements Runnable {
    private String email;
    private final AtomicInteger sendEmails = new AtomicInteger();
    private final EmailSenderService emailSenderService;

    public EmailSender(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void run() {
        emailSenderService.sendMail(email, "Reminder from Trello", "Hello, you had to finish your job!");
        sendEmails.incrementAndGet();
    }
}
