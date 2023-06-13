package spd.trello.security.extrafilter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spd.trello.configuration.UserContextHolder;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.Permission;
import spd.trello.exception.SecurityAccessException;
import spd.trello.repository.BoardRepository;
import spd.trello.repository.MemberRepository;
import spd.trello.repository.UserRepository;
import spd.trello.repository.WorkspaceRepository;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Component
public class BoardChecker extends AbstractChecker<Board, BoardRepository> {

    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public BoardChecker(UserRepository userRepository, MemberRepository memberRepository, BoardRepository entityRepository, WorkspaceRepository workspaceRepository) {
        super(userRepository, memberRepository, entityRepository);
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    protected void checkEntityAccessRights(UUID entityId, User user) {
        Board board = entityRepository.findById(entityId).orElseThrow(EntityNotFoundException::new);
        if (!board.getVisibility().name().equals("PUBLIC")) {
            findMemberBy(board, user);
        }
    }

    @Override
    protected void checkMembership(UUID entityId, User user, Permission permission) {
        Board board = entityRepository.findById(entityId).orElseThrow(EntityNotFoundException::new);
        Member member = findMemberBy(board, user);
        if (!member.getRole().getPermissions().contains(permission))
            throw new SecurityAccessException("Member does not have enough access rights");
    }

    @Override
    protected Member findMemberBy(Board entity, User user) {
        List<Member> members = UserContextHolder.getMembersContext(user.getEmail());
        return members.stream()
                .filter(member -> entity.getMembersIds().contains(member.getId()))
                .findFirst()
                .orElseThrow(() -> new SecurityAccessException("User: " + user.getEmail() + " does not have access to board"));
    }

    protected Member findMemberByParent(UUID entityId, User user) {
        Workspace workspace = workspaceRepository.findById(entityId).orElseThrow(EntityNotFoundException::new);
        List<Member> members = UserContextHolder.getMembersContext(user.getEmail());
        return members.stream()
                .filter(member -> workspace.getMembersIds().contains(member.getId()))
                .findFirst()
                .orElseThrow(() -> new SecurityAccessException("User: " + user.getEmail() + " does not have access to modify workspace"));
    }

    @Override
    protected void checkPostRequest(HttpServletRequest request, User user) {
        Board board = readFromJson(request, Board.class);
        Member member = findMemberByParent(board.getWorkspaceId(), user);
        if (!member.getRole().getPermissions().contains(Permission.WRITE))
            throw new SecurityAccessException("Member does not have enough access rights");
    }
}
