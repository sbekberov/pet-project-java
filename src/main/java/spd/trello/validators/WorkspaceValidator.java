package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Workspace;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.repository.WorkspaceRepository;

@Component
public class WorkspaceValidator extends AbstractValidator<Workspace> {

    private final WorkspaceRepository workspaceRepository;
    private final HelperValidator<Workspace> helper;

    public WorkspaceValidator(WorkspaceRepository workspaceRepository, HelperValidator<Workspace> helper) {
        this.workspaceRepository = workspaceRepository;
        this.helper = helper;
    }

    private void validateWorkspaceFields(StringBuilder exceptions, Workspace entity) {
        if (entity.getName() == null) {
            throw new BadRequestException("The name field must be filled.");
        }
        if (entity.getName().length() < 2 || entity.getName().length() > 30) {
            exceptions.append("The name field must be between 2 and 30 characters long. \n");
        }
        if (entity.getDescription() != null && (entity.getDescription().length() < 2 || entity.getDescription().length() > 255)) {
            exceptions.append("The description field must be between 2 and 255 characters long. \n");
        }
    }

    @Override
    public void validateSaveEntity(Workspace entity) {
        StringBuilder exceptions = helper.validateCreateEntity(entity);
        validateWorkspaceFields(exceptions, entity);
        helper.validateMembersIds(exceptions, entity.getMembersIds());
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Workspace entity) {
        var oldWorkspace = workspaceRepository.findById(entity.getId());
        if (oldWorkspace.isEmpty()) {
            throw new ResourceNotFoundException("Can not update non-existent workspace!");
        }
        StringBuilder exceptions = helper.validateEntityUpdate(oldWorkspace.get(), entity);
        validateWorkspaceFields(exceptions, entity);
        helper.validateMembersIds(exceptions, entity.getMembersIds());
        helper.throwException(exceptions);
    }
}
