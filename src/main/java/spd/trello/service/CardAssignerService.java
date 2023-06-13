package spd.trello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spd.trello.domain.Board;
import spd.trello.domain.Card;
import spd.trello.domain.CardList;
import spd.trello.domain.Member;
import spd.trello.domain.enums.DifficultyLevel;
import spd.trello.domain.enums.ExpertiseLevel;
import spd.trello.repository.BoardRepository;
import spd.trello.repository.CardListRepository;
import spd.trello.repository.CardRepository;
import spd.trello.repository.MemberRepository;

import java.util.*;

@Component
public class CardAssignerService {

    private final CardListRepository cardListRepository;
    private final BoardRepository boardRepository;
    private final CardService cardService;
    private final MemberRepository memberRepository;
    private CardRepository cardRepository;

    @Autowired
    public CardAssignerService(CardListRepository cardListRepository,CardRepository cardRepository, BoardRepository boardRepository,CardService cardService, MemberRepository memberRepository) {
        this.cardListRepository = cardListRepository;
        this.boardRepository = boardRepository;
        this.cardService = cardService;
        this.memberRepository = memberRepository;
        this.cardRepository = cardRepository;
    }

    private CardList getCardListFromDatabase(UUID cardListId) {
        return cardListRepository.findById(cardListId)
                .orElseThrow(() -> new RuntimeException("CardList not found"));
    }

    private Board getBoardFromDatabase(UUID boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }


    public void assignMemberOnCards(UUID cardListId1, UUID cardListId2) {
        UUID boardId = getCardListFromDatabase(cardListId1).getBoardId();
        Set<UUID> membersIds = getBoardFromDatabase(boardId).getMembersIds();

        List<Card> cardsForAssignment = cardService.getAllByCardListIds(cardListId1);
        List<Member> membersForAssignmentOnCards = memberRepository.findAllById(membersIds);

        sortCardsForAssignment(cardsForAssignment);
        sortMembersForAssignmentOnCards(membersForAssignmentOnCards);

        isMemberCapable(cardsForAssignment,membersForAssignmentOnCards,cardListId2);

    }

    private void sortCardsForAssignment(List<Card> cards) {
        Comparator<Card> comparator = Comparator
                .comparing(Card::getDifficulty)
                .thenComparingInt(Card::getEstimatedTimeForToBeDone);

        Collections.sort(cards, comparator.reversed());
    }

    private void sortMembersForAssignmentOnCards(List<Member> members) {
        Comparator<Member> comparator = Comparator
                .comparing(Member::getExpertise, Comparator.comparingInt(Enum::ordinal))
                .thenComparingInt(Member::getHoursPerBoard);

        Collections.sort(members, comparator.reversed());
    }



    private boolean isMemberCapable(List<Card> cardsForAssignment, List<Member> membersForAssignmentOnCards, UUID cardListId2) {
        for (Card card : cardsForAssignment) {
            boolean memberFound = false;

            for (Member member : membersForAssignmentOnCards) {
                ExpertiseLevel expertiseLevel = member.getExpertise();
                int hoursPerBoard = member.getHoursPerBoard();
                int estimatedTimeForToBeDone = card.getEstimatedTimeForToBeDone();

                if (expertiseLevel == ExpertiseLevel.JUNIOR) {
                    if (card.getDifficulty() == DifficultyLevel.EASY && hoursPerBoard >= estimatedTimeForToBeDone) {
                        int remainingTime = hoursPerBoard - estimatedTimeForToBeDone;
                        member.setHoursPerBoard(remainingTime);
                        card.getMembersIds().add(member.getId());
                        card.setCardListId(cardListId2);
                        cardRepository.save(card);
                        memberFound = true;
                        break;
                    }
                } else if (expertiseLevel == ExpertiseLevel.MIDDLE) {
                    if ((card.getDifficulty() == DifficultyLevel.MEDIUM || card.getDifficulty() == DifficultyLevel.EASY)
                            && hoursPerBoard >= estimatedTimeForToBeDone) {
                        int remainingTime = hoursPerBoard - estimatedTimeForToBeDone;
                        member.setHoursPerBoard(remainingTime);
                        card.getMembersIds().add(member.getId());
                        card.setCardListId(cardListId2);
                        cardRepository.save(card);
                        memberFound = true;
                        break;
                    }
                } else if (expertiseLevel == ExpertiseLevel.SENIOR) {
                    if ((card.getDifficulty() == DifficultyLevel.HARD || card.getDifficulty() == DifficultyLevel.MEDIUM || card.getDifficulty() == DifficultyLevel.EASY)
                            && hoursPerBoard >= estimatedTimeForToBeDone) {
                        int remainingTime = hoursPerBoard - estimatedTimeForToBeDone;
                        member.setHoursPerBoard(remainingTime);
                        card.getMembersIds().add(member.getId());
                        card.setCardListId(cardListId2);
                        cardRepository.save(card);
                        memberFound = true;
                        break;
                    }
                }
            }

            if (!memberFound) {
                // No suitable member found for this card
                // Proceed to the next card
                continue;
            }
        }

        return true;
    }


}

