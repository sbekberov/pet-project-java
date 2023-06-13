package spd.trello.service;

import java.util.List;
import java.util.UUID;

public interface CommonService<E> {
    E create(E entity);
    E update( E entity);
    void delete(UUID id);
    E findById(UUID id);
    List<E> getAll();
}