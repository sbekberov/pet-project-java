package spd.trello.domain;

import lombok.*;
import spd.trello.domain.common.Resource;
import spd.trello.domain.enums.ExpertiseLevel;
import spd.trello.domain.enums.Role;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "member")
public class Member extends Resource {


    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "user_id")
    @NotNull(message = "The userId field must be filled.")
    private UUID userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "expertise")
    private ExpertiseLevel expertise;
    @Column(name = "hours_per_board")
    private int hoursPerBoard=0;

    public Member() {
    }

    public Member(UUID id, UUID userId, Role role) {
        this.setId(id);
        this.userId = userId;
        this.role = role;
    }
}
