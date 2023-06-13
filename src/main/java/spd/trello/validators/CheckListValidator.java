package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.CheckList;
import spd.trello.exception.BadRequestException;
import spd.trello.repository.CardRepository;
import spd.trello.repository.CheckListRepository;


@Component
public class CheckListValidator extends AbstractValidator<CheckList> {
    private final CardRepository cardRepository;
    private final CheckListRepository checklistRepository;
    private final HelperValidator<CheckList> helper;

    public CheckListValidator(CardRepository cardRepository, CheckListRepository checkListRepository,
                              HelperValidator<CheckList> helper) {
        this.cardRepository = cardRepository;
        this.checklistRepository = checkListRepository;
        this.helper = helper;
    }

    private void checkCheckListFields(StringBuilder exceptions, CheckList entity) {
        if (entity.getName() == null) {
            throw new BadRequestException("The name field must be filled.");
        }
        if (entity.getName().length() < 2 || entity.getName().length() > 30) {
            exceptions.append("The name field must be between 2 and 30 characters long. \n");
        }
    }

    @Override
    public void validateSaveEntity(CheckList entity) {
        StringBuilder exceptions = helper.validateCreateEntity(entity);
        checkCheckListFields(exceptions, entity);
        if (entity.getCardId() == null || !cardRepository.existsById(entity.getCardId())) {
            exceptions.append("The cardId field must belong to a card. \n");
        }
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(CheckList entity) {
        var oldCheckList = checklistRepository.findById(entity.getId());
        if (oldCheckList.isEmpty()) {
            throw new BadRequestException("Can not update non-existent checkList!");
        }
        StringBuilder exceptions = helper.validateEntityUpdate(oldCheckList.get(), entity);
        checkCheckListFields(exceptions, entity);
        if (!oldCheckList.get().getCardId().equals(entity.getCardId())) {
            exceptions.append("CheckList can not be transferred to another card. \n");
        }
        helper.throwException(exceptions);
    }
}
