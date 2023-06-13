package spd.trello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.Role;
import spd.trello.security.JwtTokenProvider;
import spd.trello.service.BoardService;
import spd.trello.service.MemberService;
import spd.trello.service.WorkspaceService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/boards")
public class BoardController extends AbstractController<Board, BoardService> {

    private final BoardService service;
    private final WorkspaceService workspaceService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public BoardController(BoardService service, WorkspaceService workspaceService, MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        super(service);
        this.service = service;
        this.workspaceService = workspaceService;
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/create")
    public ResponseEntity<Board> create(@Valid @RequestBody Board entity, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        UUID userId = getUserIdFromToken(token);

        Member newMember = new Member();
        newMember.setUserId(userId);
        newMember.setRole(Role.ADMIN);
        memberService.create(newMember);

        Board result = new Board(entity);
        result.setWorkspaceId(entity.getWorkspaceId());
        result.getMembersIds().add(newMember.getId());
        service.create(result);


        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    private UUID getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserId(token);
    }
}
