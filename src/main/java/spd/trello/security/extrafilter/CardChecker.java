package spd.trello.security.extrafilter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spd.trello.configuration.UserContextHolder;
import spd.trello.domain.*;
import spd.trello.domain.enums.Permission;
import spd.trello.exception.SecurityAccessException;
import spd.trello.repository.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CardChecker extends AbstractChecker<Card, CardRepository> {

    private final CardListRepository cardListRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public CardChecker(UserRepository userRepository, MemberRepository memberRepository, CardRepository entityRepository, CardListRepository cardListRepository, BoardRepository boardRepository) {
        super(userRepository, memberRepository, entityRepository);
        this.cardListRepository = cardListRepository;
        this.boardRepository = boardRepository;
    }

    @Override
    protected void checkMembership(UUID entityId, User user, Permission permission) {
       /* Card card = entityRepository.findById(entityId).orElseThrow(EntityNotFoundException::new);
        Member member = findMemberBy(card, user);
        if (!member.getRole().getPermissions().contains(permission))
            throw new SecurityAccessException("Member does not have enough access rights");*/
    }

    @Override
    protected Member findMemberBy(Card entity, User user) {
       /* List<Member> members = UserContextHolder.getMembersContext(user.getEmail());
        return members.stream()
                .filter(member -> entity.getMembersIds().contains(member.getId()))
                .findFirst()
                .orElseThrow(() -> new SecurityAccessException("User: " + user.getEmail() + " does not have access to card"));*/
        return new Member();
    }

    @Override
    protected void checkPostRequest(HttpServletRequest request, User user) {
        Card card = readFromJson(request, Card.class);
        if (cardListRepository.existsById(card.getCardListId())) {
            Optional<CardList> cardListOptional = cardListRepository.findById(card.getCardListId());
            if (cardListOptional.isPresent()) {
                Member member = findMemberByParent(cardListOptional.get().getBoardId(), user);
                member.getRole().getPermissions().contains(Permission.UPDATE);
            }
        }
    }

    protected Member findMemberByParent(UUID entityId, User user) {
        Board board = boardRepository.findById(entityId).orElseThrow(EntityNotFoundException::new);
        List<Member> members = UserContextHolder.getMembersContext(user.getEmail());
        return members.stream()
                .filter(member -> board.getMembersIds().contains(member.getId()))
                .findFirst()
                .orElseThrow(() -> new SecurityAccessException("User: " + user.getEmail() + " does not have access to create card"));
    }

    @Override
    protected void checkEntityAccessRights(UUID entityId, User user) {
    }
}
