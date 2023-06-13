package spd.trello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spd.trello.domain.CheckableItem;
import spd.trello.repository.CheckableItemRepository;
import spd.trello.validators.CheckableItemValidator;

import java.util.UUID;


@Service
public class CheckableItemService extends AbstractService<CheckableItem, CheckableItemRepository, CheckableItemValidator> {
    @Autowired
    public CheckableItemService(CheckableItemRepository repository, CheckableItemValidator checkableItemValidator) {
        super(repository, checkableItemValidator);
    }

    public void deleteCheckableItemsForCheckList(UUID checkListId) {
        repository.findAllByChecklistId(checkListId).forEach(checkableItem -> delete(checkableItem.getId()));
    }
}


