package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.common.Domain;


@Component
public abstract class AbstractValidator<T extends Domain> implements CommonValidator<T> {
}
