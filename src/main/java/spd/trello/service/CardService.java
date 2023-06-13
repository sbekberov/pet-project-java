package spd.trello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spd.trello.domain.Board;
import spd.trello.domain.Comment;
import spd.trello.exception.BadRequestException;
import spd.trello.reminder.ReminderScheduler;
import spd.trello.domain.Card;
import spd.trello.repository.CardRepository;
import spd.trello.validators.CardValidator;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CardService extends AbstractService<Card, CardRepository, CardValidator> {

    public final ReminderScheduler reminderScheduler;
    private final CheckListService checkListService;

    private final CommentService commentService;
    private final AttachmentService attachmentService;

    @Autowired
    public CardService(CardRepository repository, ReminderScheduler reminderScheduler, CheckListService checkListService, AttachmentService attachmentService, CommentService commentService, CardValidator cardValidator) {
        super(repository, cardValidator);
        this.reminderScheduler = reminderScheduler;
        this.checkListService = checkListService;
        this.commentService = commentService;
        this.attachmentService = attachmentService;

    }

    @Override
    public Card update(Card entity) {
        Card oldCard = findById(entity.getId());

        if (entity.getName() == null) {
            entity.setName(oldCard.getName());
        }

        if (entity.getDescription() == null && oldCard.getDescription() != null) {
            entity.setDescription(oldCard.getDescription());
        }

        if (entity.getCardListId() == null && oldCard.getCardListId() != null) {
            entity.setCardListId(oldCard.getCardListId());
        }

        if (entity.getMembersIds() == null && oldCard.getMembersIds() != null) {
            entity.setMembersIds(oldCard.getMembersIds());
        }

        if (entity.getReminder() == null && oldCard.getReminder() != null) {
            entity.setReminder(oldCard.getReminder());
        }
        if (entity.getAttachmentIds() == null && oldCard.getAttachmentIds() != null) {
            entity.setAttachmentIds(oldCard.getAttachmentIds());
        }
        if (entity.getChecklists() == null && oldCard.getChecklists() != null) {
            entity.setChecklists(oldCard.getChecklists());
        }
        if (entity.getLabels() == null && oldCard.getLabels() != null) {
            entity.setLabels(oldCard.getLabels());
        }
        if (entity.getComments() == null && oldCard.getComments() != null) {
            entity.setComments(oldCard.getComments());
        }

        if (entity.getIndex() == 0 && oldCard.getIndex() != 0) {
            entity.setIndex(oldCard.getIndex());
        }

        if (entity.getEstimatedTimeForToBeDone() == 0 && oldCard.getEstimatedTimeForToBeDone() != 0) {
            entity.setEstimatedTimeForToBeDone(oldCard.getEstimatedTimeForToBeDone());
        }

        if (entity.getDifficulty() == null && oldCard.getDifficulty() != null) {
            entity.setDifficulty(oldCard.getDifficulty());
        }

        entity.setUpdatedDate(LocalDateTime.now().withNano(0));
        entity.setCreatedBy(oldCard.getCreatedBy());
        entity.setCreatedDate(oldCard.getCreatedDate());

        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        checkListService.deleteCheckListsForCard(id);
        commentService.deleteCommentsForCard(id);
        attachmentService.deleteAttachmentsForCard(id);
        super.delete(id);
    }

    public void deleteMemberInCards(UUID memberId) {
        List<Card> cards = repository.findAllByMembersIdsEquals(memberId);
        for (Card card : cards) {
            Set<UUID> membersId = card.getMembersIds();
            membersId.remove(memberId);
        }
    }

    public void deleteCardsForCardList(UUID cardListId) {
        repository.findAllByCardListId(cardListId).forEach(card -> delete(card.getId()));
    }

    public List<Card> getAllByCardListIds(UUID cardListIds) {
        return repository.findAllByCardListId(cardListIds);
    }



}
