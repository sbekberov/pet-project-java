package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.User;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.repository.UserRepository;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator extends AbstractValidator<User> {

    private final UserRepository repository;

    public UserValidator(UserRepository repository) {
        this.repository = repository;
    }

    private void validateNullFields(User entity) {
        if (entity.getFirstName() == null || entity.getLastName() == null || entity.getEmail() == null) {
            throw new BadRequestException("The firstname, lastname and email fields must be filled.");
        }
    }

    private void validateUserFields(StringBuilder exceptions, User entity) {
        validateNullFields(entity);
        if (entity.getFirstName().length() < 2 || entity.getFirstName().length() > 30) {
            exceptions.append("The firstname field must be between 2 and 30 characters long.");
        }
        if (entity.getLastName().length() < 2 || entity.getLastName().length() > 30) {
            exceptions.append("The lastname field must be between 2 and 30 characters long.");
        }
        throwException(exceptions);
    }

    public void throwException(StringBuilder exceptions) {
        if (exceptions.length() != 0) {
            throw new BadRequestException(exceptions.toString());
        }
    }

    @Override
    public void validateSaveEntity(User entity) {
        StringBuilder exceptions = new StringBuilder();
        validateNullFields(entity);
        if (repository.existsByEmail(entity.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }
        Pattern email = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = email.matcher(entity.getEmail());

        if (!matcher.find()) {
            exceptions.append("The email field should look like email. For example : bekberov@gmail.com ");
        }
        validateUserFields(exceptions, entity);
    }

    @Override
    public void validateUpdateEntity(User entity) {
        StringBuilder exceptions = new StringBuilder();
        var oldUser = repository.findById(entity.getId());
        if (oldUser.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update non-existent user!");
        }
        if (!oldUser.get().getEmail().equals(entity.getEmail())) {
            exceptions.append("The email field can not be updated!");
        }
        validateUserFields(exceptions, entity);
    }

}