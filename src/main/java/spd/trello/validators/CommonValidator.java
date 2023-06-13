package spd.trello.validators;

import spd.trello.domain.common.Domain;


public interface CommonValidator<T extends Domain> {
    void validateSaveEntity(T entity);
    void validateUpdateEntity(T entity);
}
