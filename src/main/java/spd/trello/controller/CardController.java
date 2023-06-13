package spd.trello.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spd.trello.domain.Card;
import spd.trello.service.CardService;


@RestController
@RequestMapping("/cards")
public class CardController extends AbstractController<Card, CardService> {
    public CardController (CardService service) {
        super(service);
    }
}
