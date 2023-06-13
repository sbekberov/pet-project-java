package spd.trello.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spd.trello.domain.CheckList;
import spd.trello.service.CheckListService;


@RestController
@RequestMapping("/checkLists")
public class CheckListController extends AbstractController<CheckList, CheckListService> {
    public CheckListController(CheckListService service) {
        super(service);
    }
}
