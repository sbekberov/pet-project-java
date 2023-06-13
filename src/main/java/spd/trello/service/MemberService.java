package spd.trello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spd.trello.domain.Member;
import spd.trello.exception.BadRequestException;
import spd.trello.repository.MemberRepository;
import spd.trello.validators.MemberValidator;


import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class MemberService extends AbstractService<Member, MemberRepository, MemberValidator> {
    private final WorkspaceService workspaceService;
    private final CardService cardService;
    private final BoardService boardService;

    @Autowired
    public MemberService(MemberRepository repository, WorkspaceService workspaceService, BoardService boardService, CardService cardService, MemberValidator memberValidator) {
        super(repository, memberValidator);
        this.workspaceService = workspaceService;
        this.cardService = cardService;
        this.boardService = boardService;
    }

    @Override
    public Member update(Member entity) {
        Member oldMember = findById(entity.getId());
        entity.setUpdatedDate(LocalDateTime.now().withNano(0));
        entity.setCreatedBy(oldMember.getCreatedBy());
        entity.setCreatedDate(oldMember.getCreatedDate());
        entity.setUserId(oldMember.getUserId());

        if (entity.getRole() == null) {
            entity.setRole(oldMember.getRole());
        }

        if (entity.getHoursPerBoard() == 0 && oldMember.getHoursPerBoard() != 0) {
            entity.setHoursPerBoard(oldMember.getHoursPerBoard());
        }

        if (entity.getExpertise() == null && oldMember.getExpertise() != null) {
            entity.setExpertise(oldMember.getExpertise());
        }

        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        workspaceService.deleteMemberInWorkspaces(id);
        boardService.deleteMemberInBoards(id);
        cardService.deleteMemberInCards(id);
        super.delete(id);
    }

    public void deleteMemberForUser(UUID userId) {
        repository.findByUserId(userId).forEach(member -> delete(member.getId()));
    }


}