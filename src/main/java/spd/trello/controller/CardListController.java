package spd.trello.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spd.trello.domain.CardList;
import spd.trello.service.CardListService;


@RestController
@RequestMapping("/cardLists")
public class CardListController extends AbstractController<CardList, CardListService> {
    public CardListController (CardListService service) {
        super(service);
    }
}
