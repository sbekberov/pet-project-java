package spd.trello.configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.repository.MemberRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserContextHolder implements Runnable {
    private static Map<String, List<Member>> userContext = new ConcurrentHashMap();
    private User user;
    private MemberRepository repository;

    public UserContextHolder(User user, MemberRepository repository) {
        this.user = user;
        this.repository = repository;
    }

    @Override
    public void run() {
        UUID id = user.getId();
        userContext.put(user.getEmail(), repository.findByUserId(id));
    }

    public static List<Member> getMembersContext(String key) {
        return userContext.get(key);
    }

    public static List<UUID> getMembersIdContext(String key) {
        return userContext.get(key).stream().map(Member::getId).collect(Collectors.toList());
    }

    public static void cleanUserContext(String key) {
        userContext.remove(key);
    }
}
