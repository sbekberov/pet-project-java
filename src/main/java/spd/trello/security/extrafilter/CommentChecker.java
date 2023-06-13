package spd.trello.security.extrafilter;

import org.springframework.stereotype.Component;
import spd.trello.configuration.UserContextHolder;
import spd.trello.domain.*;
import spd.trello.domain.enums.Permission;
import spd.trello.exception.SecurityAccessException;
import spd.trello.repository.*;
import spd.trello.service.BoardService;
import spd.trello.service.CardListService;
import spd.trello.service.CardService;
import spd.trello.service.CommentService;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Component
public class CommentChecker extends AbstractChecker<Comment, CommentRepository> {

    private final BoardService boardService;
    private final CardService cardService;
    private final CardListService cardListService;

    public CommentChecker(UserRepository userRepository, MemberRepository memberRepository, CommentRepository entityRepository,
                          BoardService boardService, CardListService cardListService,CardService cardService) {
        super(userRepository, memberRepository, entityRepository);
        this.boardService = boardService;
        this.cardService = cardService;
        this.cardListService = cardListService;
    }

    @Override
    protected void checkPostRequest(HttpServletRequest request, User user) {
        Comment comment = readFromJson(request, Comment.class);
        findMemberBy(comment, user);
    }

    @Override
    protected void checkMembership(UUID entityId, User user, Permission permission) {
        Comment comment = getComment(entityId);
        Member member = findMemberBy(comment, user);
        if (!user.getEmail().equals(comment.getCreatedBy()) ||
                !member.getRole().getPermissions().contains(Permission.WRITE))
            throw new SecurityAccessException("Member does not have enough access rights");
    }

    @Override
    protected Member findMemberBy(Comment entity, User user) {
        Card card = cardService.findById(entity.getCardId());
        CardList cardList = cardListService.findById(card.getCardListId());
        Board board = boardService.findById(cardList.getBoardId());
        List<Member> members = UserContextHolder.getMembersContext(user.getEmail());
        return members.stream()
                .filter(member -> board.getMembersIds().contains(member.getId()))
                .findFirst()
                .orElseThrow(() -> new SecurityAccessException("User: " + user.getEmail() + " does not have access to write comment"));
    }

    private Comment getComment(UUID id) {
        return entityRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    protected void checkEntityAccessRights(UUID entityId, User user) {
        findMemberBy(getComment(entityId), user);
    }
}
