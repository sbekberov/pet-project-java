package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends AbstractRepository<User> {
    User findByEmail(String email);
    User findUserById (UUID userId);

    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);

}
