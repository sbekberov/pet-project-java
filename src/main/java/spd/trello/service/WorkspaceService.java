package spd.trello.service;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spd.trello.domain.Workspace;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.repository.WorkspaceRepository;
import spd.trello.validators.WorkspaceValidator;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class WorkspaceService extends AbstractService<Workspace, WorkspaceRepository, WorkspaceValidator> {

    private final BoardService boardService;

    @Autowired
    public WorkspaceService(WorkspaceRepository repository, BoardService boardService,WorkspaceValidator workspaceValidator) {
        super(repository,workspaceValidator);
        this.boardService = boardService;
    }


    @Override

    public Workspace update(Workspace entity) {
        Workspace oldWorkspace = findById(entity.getId());
        entity.setUpdatedDate(LocalDateTime.now().withNano(0));
        entity.setCreatedBy(oldWorkspace.getCreatedBy());
        entity.setCreatedDate(oldWorkspace.getCreatedDate());


        if (entity.getVisibility() == null && oldWorkspace.getVisibility() != null) {
            entity.setVisibility(oldWorkspace.getVisibility());
        }

        if ((entity.getMembersIds() == null || entity.getMembersIds().isEmpty()) && oldWorkspace.getMembersIds() != null) {
            entity.setMembersIds(oldWorkspace.getMembersIds());
        }


        if (entity.getName() == null) {
            entity.setName(oldWorkspace.getName());
        }
        if (entity.getDescription() == null && oldWorkspace.getDescription() != null) {
            entity.setDescription(oldWorkspace.getDescription());
        }

        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        boardService.deleteBoardForWorkspace(id);
        super.delete(id);
    }

    public void deleteMemberInWorkspaces(UUID memberId) {
        List<Workspace> workspaces = repository.findAllByMembersIdsEquals(memberId);
        for (Workspace workspace : workspaces) {
            Set<UUID> membersId = workspace.getMembersIds();
            membersId.remove(memberId);
            if (workspace.getMembersIds().isEmpty()) {
                delete(workspace.getId());
            }
        }
    }



}