package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.CheckList;

import java.util.List;
import java.util.UUID;

@Repository
public interface CheckListRepository extends AbstractRepository<CheckList>{
    List<CheckList> findAllByCardId(UUID cardId);
}
