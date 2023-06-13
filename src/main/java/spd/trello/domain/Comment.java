package spd.trello.domain;

import lombok.*;
import spd.trello.domain.common.Resource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table
public class Comment extends Resource {
    @Column(name = "text")
    @NotNull(message = "The text field must be filled.")
    @Size(min = 1, max = 999, message = "The text field must be between 1 and 999 characters long.")
    private String text;
    @Column(name = "card_id")
    private UUID cardId;
    @Column(name = "member_id")
    private UUID memberId;

}
