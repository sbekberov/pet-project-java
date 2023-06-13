package spd.trello.security.extrafilter;

import org.springframework.stereotype.Component;
import spd.trello.configuration.UserContextHolder;
import spd.trello.domain.Board;
import spd.trello.domain.CardList;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.enums.Permission;
import spd.trello.exception.SecurityAccessException;
import spd.trello.repository.BoardRepository;
import spd.trello.repository.CardListRepository;
import spd.trello.repository.MemberRepository;
import spd.trello.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Component
public class CardListChecker extends AbstractChecker<CardList, CardListRepository> {

    private final BoardRepository boardRepository;

    public CardListChecker(UserRepository userRepository, MemberRepository memberRepository, CardListRepository entityRepository, BoardRepository boardRepository) {
        super(userRepository, memberRepository, entityRepository);
        this.boardRepository = boardRepository;
    }

    @Override
    protected void checkPostRequest(HttpServletRequest request, User user) {
        CardList cardList = readFromJson(request, CardList.class);
        Member member = getMemberByParent(user, cardList.getBoardId());
        if (!member.getRole().getPermissions().contains(Permission.UPDATE))
            throw new SecurityAccessException("Member does not have enough access rights");
    }

    private Member getMemberByParent(User user, UUID parentId) {
        Board board = boardRepository.findById(parentId).orElseThrow(EntityNotFoundException::new);
        List<Member> members = UserContextHolder.getMembersContext(user.getEmail());
        return members.stream()
                .filter(member -> board.getMembersIds().contains(member.getId()))
                .findFirst()
                .orElseThrow(() -> new SecurityAccessException("User: " + user.getEmail() + " does not have access to workspace"));
    }

    @Override
    protected void checkMembership(UUID entityId, User user, Permission permission) {}

    @Override
    protected Member findMemberBy(CardList entity, User user) {
        return null;
    }

    @Override
    protected void checkEntityAccessRights(UUID entityId, User user) {}
}
