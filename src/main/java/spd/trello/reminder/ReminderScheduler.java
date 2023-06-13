package spd.trello.reminder;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spd.trello.domain.Card;
import spd.trello.domain.Member;
import spd.trello.domain.Reminder;
import spd.trello.domain.User;
import spd.trello.repository.CardRepository;
import spd.trello.repository.MemberRepository;
import spd.trello.repository.ReminderRepository;
import spd.trello.repository.UserRepository;
import spd.trello.service.EmailSenderService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@EnableScheduling
@Log4j2
public class ReminderScheduler {

    private final ReminderRepository repository;
    private final EmailSenderService emailSenderService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public ReminderScheduler(ReminderRepository repository, EmailSenderService emailSenderService, CardRepository cardRepository, MemberRepository memberRepository, UserRepository userRepository) {
        this.repository = repository;
        this.emailSenderService = emailSenderService;
        this.cardRepository = cardRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "${cron.expression}")
    public void runReminder() {
        List<Reminder> reminders = repository.findAllByRemindOnBeforeAndActive(LocalDateTime.now().withNano(0), true);
        reminders.forEach(reminder -> {
            List<String> emails = getUserEmails(reminder);
            emails.forEach(email -> {
                EmailSender emailSender = new EmailSender(emailSenderService);
                emailSender.setEmail(email);
                executorService.submit(emailSender);
                log.info("Email sent to {}", email);
            });
            reminder.setActive(false);
            repository.save(reminder);
            log.info("Time to finish! " + Objects.requireNonNull(reminder).getId());
        });
    }

    public List<String> getUserEmails(Reminder reminder) {
        Card card = cardRepository.findCardByReminder(reminder);
        Set<UUID> memberIds = card.getMembersIds();
        List<String> userEmails = new ArrayList<>();
        if (!memberIds.isEmpty()) {
            List<Member> members = memberRepository.getByIdIn(new ArrayList<>(memberIds));

            for (Member member : members) {
                UUID userId = member.getUserId();
                User user = userRepository.findUserById(userId);
                userEmails.add(user.getEmail());
            }
        }

        return userEmails;
    }
}
