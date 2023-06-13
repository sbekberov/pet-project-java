package spd.trello.security.extrafilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spd.trello.configuration.UserContextHolder;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.Permission;
import spd.trello.domain.enums.WorkspaceVisibility;
import spd.trello.exception.SecurityAccessException;
import spd.trello.repository.MemberRepository;
import spd.trello.repository.UserRepository;
import spd.trello.repository.WorkspaceRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Component
public class WorkspaceChecker extends AbstractChecker<Workspace, WorkspaceRepository> {

    @Autowired
    public WorkspaceChecker(UserRepository userRepository, MemberRepository memberRepository, WorkspaceRepository entityRepository) {
        super(userRepository, memberRepository, entityRepository);
    }

    @Override
    protected void checkMembership(UUID entityId, User user, Permission permission) {
        Workspace workspace = entityRepository.findById(entityId).get();
        Member member = findMemberBy(workspace, user);
        if (!member.getRole().getPermissions().contains(permission))
            throw new SecurityAccessException("Member does not have enough access rights to modify workspace");
    }

    @Override
    protected Member findMemberBy(Workspace workspace, User user) {
        List<Member> members = UserContextHolder.getMembersContext(user.getEmail());
        return members.stream()
                .filter(member -> workspace.getMembersIds().contains(member.getId()))
                .findFirst()
                .orElseThrow(() -> new SecurityAccessException("User: " + user.getEmail() + " does not have access to workspace"));
    }

    @Override
    protected void checkEntityAccessRights(UUID entityId, User user) {
        Workspace workspace = entityRepository.findById(entityId).get();
        if (workspace.getVisibility() == WorkspaceVisibility.PRIVATE)
            findMemberBy(workspace, user);
    }

    @Override
    protected void checkPostRequest(HttpServletRequest request, User user) {
    }
}
