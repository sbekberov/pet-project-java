package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.CardList;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.repository.BoardRepository;
import spd.trello.repository.CardListRepository;

@Component
public class CardListValidator extends AbstractValidator<CardList> {

    private final BoardRepository boardRepository;
    private final CardListRepository cardListRepository;
    private final HelperValidator<CardList> helper;

    public CardListValidator(BoardRepository boardRepository, CardListRepository cardListRepository,
                             HelperValidator<CardList> helper) {
        this.boardRepository = boardRepository;
        this.cardListRepository = cardListRepository;
        this.helper = helper;
    }

    private void validateName(StringBuilder exception, CardList entity) {
        if (entity.getName().length() < 2 || entity.getName().length() > 30) {
            exception.append("Name should be between 2 and 30 characters! \n");
        }
    }

    @Override
    public void validateSaveEntity(CardList entity) {
        if (entity.getArchived()) {
            throw new BadRequestException("You cannot create an archived card list.");
        }
        StringBuilder exceptions = helper.validateCreateEntity(entity);
        validateName(exceptions, entity);
        if (!boardRepository.existsById(entity.getBoardId())) {
            throw new ResourceNotFoundException("The boardId field must belong to a board.");
        }
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(CardList entity) {
        var oldCardList = cardListRepository.findById(entity.getId());
        if (oldCardList.isEmpty()) {
            throw new BadRequestException("Can not update non-existent card list!");
        }
        if (oldCardList.get().getArchived() && entity.getArchived()) {
            throw new BadRequestException("Archived CardList cannot be updated.");
        }
        StringBuilder exceptions = helper.validateEntityUpdate(oldCardList.get(), entity);
        validateName(exceptions, entity);
        if (!oldCardList.get().getBoardId().equals(entity.getBoardId())) {
            exceptions.append("CardList cannot be transferred to another board. \n");
        }
        helper.throwException(exceptions);
    }

}
