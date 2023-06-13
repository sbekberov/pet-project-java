package spd.trello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.AlgorithmDTO;
import spd.trello.service.CardAssignerService;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/algorithm")
public class CardAssignerController {

    private final CardAssignerService cardAssignerService;

    @Autowired
    public CardAssignerController(CardAssignerService cardAssignerService) {
        this.cardAssignerService = cardAssignerService;
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignMembersOnCards(@RequestBody AlgorithmDTO request) {


        cardAssignerService.assignMemberOnCards(request.getCardListIdBacklog(), request.getCardListIdToDo());

        return ResponseEntity.status(HttpStatus.OK).body("Members assigned successfully.");
    }

}

