package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Card;
import spd.trello.domain.CardList;
import spd.trello.domain.Reminder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CardRepository extends AbstractRepository<Card>{
    List<Card> findAllByMembersIdsEquals(UUID memberId);
    List<Card> findAllByCardListId(UUID cardListId);
    Card findCardByReminder(Reminder reminder);

}
