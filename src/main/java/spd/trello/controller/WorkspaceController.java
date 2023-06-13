package spd.trello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.Role;
import spd.trello.security.JwtTokenProvider;
import spd.trello.service.MemberService;
import spd.trello.service.WorkspaceService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController extends AbstractController<Workspace, WorkspaceService>
 {

    private final WorkspaceService service;
    private final JwtTokenProvider jwtTokenProvider;
    private MemberService memberService;

    @Autowired
    public WorkspaceController(WorkspaceService service, JwtTokenProvider jwtTokenProvider, MemberService memberService) {
        super(service);
        this.service = service;
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberService = memberService;
    }

    @PostMapping("/create")
    public ResponseEntity<Workspace> create(@Valid @RequestBody Workspace entity, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        UUID userId = getUserIdFromToken(token);

        Member newMember = new Member();
        newMember.setUserId(userId);
        newMember.setRole(Role.ADMIN);
        memberService.create(newMember);

        Workspace result = new Workspace(entity);
        result.getMembersIds().add(newMember.getId());
        service.create(result);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    private UUID getUserIdFromToken(String token) {
        UUID userId = jwtTokenProvider.getUserId(token);
        return userId;
    }



}

