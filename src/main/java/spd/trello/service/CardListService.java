package spd.trello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spd.trello.domain.Card;
import spd.trello.domain.CardList;
import spd.trello.domain.Member;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.repository.CardListRepository;
import spd.trello.validators.CardListValidator;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CardListService extends AbstractService<CardList, CardListRepository, CardListValidator> {

    private final CardService cardService;

    @Autowired
    public CardListService(CardListRepository repository, CardService cardService, CardListValidator cardListValidator) {
        super(repository,cardListValidator);
        this.cardService = cardService;
    }

    @Override
    public CardList update(CardList entity) {
        CardList oldCardList = findById(entity.getId());

        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("Not found updated by!");
        }

        if (entity.getDescription() == null && oldCardList.getDescription() != null) {
            entity.setDescription(oldCardList.getDescription());
        }
        if (entity.getIndex() == 0 && oldCardList.getIndex() != 0) {
            entity.setIndex(oldCardList.getIndex());
        }

        entity.setUpdatedDate(LocalDateTime.now().withNano(0));
        entity.setCreatedBy(oldCardList.getCreatedBy());
        entity.setCreatedDate(oldCardList.getCreatedDate());
        entity.setBoardId(oldCardList.getBoardId());
        if (entity.getName() == null) {
            entity.setName(oldCardList.getName());
        }
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        cardService.deleteCardsForCardList(id);
        super.delete(id);
    }

    public void deleteCardListsForBoard(UUID boardId) {
        repository.findAllByBoardId(boardId).forEach(cardList -> delete(cardList.getId()));
    }


}