package spd.trello.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spd.trello.domain.User;
import spd.trello.exception.BadRequestException;
import spd.trello.repository.UserRepository;
import spd.trello.validators.UserValidator;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService extends AbstractService<User, UserRepository, UserValidator> {

    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, UserValidator userValidator, MemberService memberService) {
        super(repository, userValidator);
        this.passwordEncoder = passwordEncoder;
        this.memberService = memberService;
    }

    public User register(User user) {
        encodePassword(user);
        User result = super.create(user);
        return result;
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    private void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    @Override
    public User update(User entity) {
        User oldUser = findById(entity.getId());
        entity.setUpdatedDate(LocalDateTime.now().withNano(0));
        entity.setCreatedBy(oldUser.getCreatedBy());
        entity.setCreatedDate(oldUser.getCreatedDate());

        if (entity.getFirstName() == null && oldUser.getFirstName() != null) {
            entity.setFirstName(oldUser.getFirstName());
        }
        if (entity.getLastName() == null && oldUser.getLastName() != null) {
            entity.setLastName(oldUser.getLastName());
        }
        if (entity.getPassword() == null && oldUser.getPassword() != null) {
            entity.setPassword(oldUser.getPassword());
        }

        if (entity.getEmail() != oldUser.getEmail()) {
            throw new BadRequestException("Email can't be changed");
        }

        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        memberService.deleteMemberForUser(id);
        super.delete(id);
    }

}
