package spd.trello.security.extrafilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import spd.trello.configuration.UserContextHolder;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.common.Domain;
import spd.trello.domain.enums.Permission;
import spd.trello.repository.AbstractRepository;
import spd.trello.repository.MemberRepository;
import spd.trello.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractChecker<E extends Domain, R extends AbstractRepository<E>> implements ExtraAuthorizationChecker<E> {

    UserRepository userRepository;
    MemberRepository memberRepository;
    R entityRepository;
    private final Pattern regex = Pattern.compile("(/\\w+[/a-z]/)");
    ObjectMapper mapper = new ObjectMapper();

    public AbstractChecker(UserRepository userRepository, MemberRepository memberRepository, R entityRepository) {
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.entityRepository = entityRepository;
    }

    @Override
    public void checkAuthority(HttpServletRequest request) {
        String uri = request.getRequestURI();
        User user = getAuthorizedUser();
        String method = request.getMethod();
        switch (method) {
            case "GET":
                if (regex.matcher(uri).find())
                    checkEntityAccessRights(getIdFromRequest(uri), user);
                break;
            case "PUT":
                checkMembership(getIdFromRequest(uri), user, Permission.UPDATE);
                break;
            case "DELETE":
                checkMembership(getIdFromRequest(uri), user, Permission.WRITE);
                break;
            case "POST":
                if (uri.contains("/add_member"))
                    checkMembership(getSpecialIdFromRequest(uri), user, Permission.WRITE);
                else
                    checkPostRequest(request, user);
                break;
        }
    }

    protected abstract void checkPostRequest(HttpServletRequest request, User user);

    protected abstract void checkMembership(UUID entityId, User user, Permission permission);

    protected abstract Member findMemberBy(E entity, User user);

    protected abstract void checkEntityAccessRights(UUID entityId, User user);

    protected E readFromJson(HttpServletRequest request, Class<E> clazz) {
        try {
            return mapper.readValue(request.getInputStream(), clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    private UUID getIdFromRequest(String request) {
        return UUID.fromString(request.replaceAll(regex.pattern(), ""));
    }

    private UUID getSpecialIdFromRequest(String request) {
        Matcher pattern = Pattern.compile("(\\w|\\d)+-(\\w|\\d){4}-(\\w|\\d){4}-(\\w|\\d){4}-(\\w|\\d)+").matcher(request);
        return UUID.fromString(pattern.group());
    }

    private User getAuthorizedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userByEmail = userRepository.findUserByEmail(authentication.getName()).get();
        UserContextHolder contextHolder = new UserContextHolder(userByEmail, memberRepository);
        try {
            Thread thread = new Thread(contextHolder);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            throw new IllegalArgumentException(e);
        }
        return userByEmail;
    }
}
