package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.CheckableItem;
import spd.trello.exception.BadRequestException;
import spd.trello.repository.CheckListRepository;
import spd.trello.repository.CheckableItemRepository;


@Component
public class CheckableItemValidator extends AbstractValidator<CheckableItem> {
    private final CheckListRepository checkListRepository;
    private final CheckableItemRepository checkableItemRepository;

    public CheckableItemValidator(CheckListRepository checkListRepository, CheckableItemRepository checkableItemRepository) {
        this.checkListRepository = checkListRepository;
        this.checkableItemRepository = checkableItemRepository;
    }

    private void checkCheckableItemFields(StringBuilder exceptions, CheckableItem entity) {
        if (entity.getName() == null) {
            throw new BadRequestException("The name field must be filled.");
        }
        if (entity.getName().length() < 2 || entity.getName().length() > 30) {
            exceptions.append("The name field must be between 2 and 30 characters long. \n");
        }
    }

    @Override
    public void validateSaveEntity(CheckableItem entity) {
        StringBuilder exceptions = new StringBuilder();
        if (entity.getChecklistId() == null || !checkListRepository.existsById(entity.getChecklistId())) {
            throw new BadRequestException("The checkListId field must belong to a checklist.");
        }
        checkCheckableItemFields(exceptions, entity);
        throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(CheckableItem entity) {
        var oldCheckableItem = checkableItemRepository.findById(entity.getId());
        if (!oldCheckableItem.get().getChecklistId().equals(entity.getChecklistId())) {
            throw new BadRequestException("CheckableItem cannot be transferred to another checkList.");
        }
        StringBuilder exceptions = new StringBuilder();
        checkCheckableItemFields(exceptions, entity);
        throwException(exceptions);
    }

    private void throwException(StringBuilder exceptions) {
        if (exceptions.length() != 0) {
            throw new BadRequestException(exceptions.toString());
        }
    }
}
