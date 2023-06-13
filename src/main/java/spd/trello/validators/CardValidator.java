package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Card;
import spd.trello.domain.Reminder;
import spd.trello.exception.BadRequestException;
import spd.trello.repository.CardListRepository;
import spd.trello.repository.CardRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Component
public class CardValidator extends AbstractValidator<Card> {
    private final CardListRepository cardListRepository;
    private final CardRepository cardRepository;
    private final HelperValidator<Card> helper;

    public CardValidator(CardListRepository cardListRepository,
                         CardRepository cardRepository, HelperValidator<Card> helper) {
        this.cardListRepository = cardListRepository;
        this.cardRepository = cardRepository;
        this.helper = helper;
    }

    private void checkCardFields(StringBuilder exceptions, Card entity) {
        if (entity.getName() == null) {
            throw new BadRequestException("The name field must be filled.");
        }
        if (entity.getName().length() < 2 || entity.getName().length() > 30) {
            exceptions.append("The name field must be between 2 and 30 characters long. \n");
        }
        if (entity.getDescription() != null &&
                (entity.getDescription().length() < 2 || entity.getDescription().length() > 255)) {
            exceptions.append("The description field must be between 2 and 255 characters long. \n");
        }
    }

    @Override
    public void validateSaveEntity(Card entity) {
        if (entity.getArchived()) {
            throw new BadRequestException("You cannot create an archived card.");
        }
        StringBuilder exceptions = helper.validateCreateEntity(entity);
        checkCardFields(exceptions, entity);
        if (!cardListRepository.existsById(entity.getCardListId())) {
            exceptions.append("The cardListId field must belong to a CardList.");
        }
        validateTime(exceptions,entity.getReminder());
        helper.validateMembersIds(exceptions, entity.getMembersIds());
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Card entity) {
        var oldCard = cardRepository.findById(entity.getId());
        if (oldCard.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent card!");
        }
        if (oldCard.get().getArchived() && entity.getArchived()) {
            throw new BadRequestException("Archived Card cannot be updated.");
        }
        StringBuilder exceptions = helper.validateEntityUpdate(oldCard.get(), entity);
        checkCardFields(exceptions, entity);
        if (!oldCard.get().getCardListId().equals(entity.getCardListId())) {
            exceptions.append("Card can not be transferred to another CardList. \n");
        }
        helper.validateMembersIds(exceptions, entity.getMembersIds());
        helper.throwException(exceptions);
    }


    public void validateTime(StringBuilder exceptions, Reminder entity) {
        if(entity==null){
            return;
        }
        if (entity.getStart().isAfter(entity.getRemindOn()) || entity.getFinish().isBefore(entity.getRemindOn())) {
            exceptions.append("The remindOn should be between start and finish. \n");
        }
        if (entity.getFinish().isBefore(entity.getStart())) {
            exceptions.append("The finish had to be after start. \n");
        }
        if (entity.getFinish().isBefore(LocalDateTime.now().withNano(0))) {
            exceptions.append("You cannot create an overdue task. \n");
        }
    }
}
