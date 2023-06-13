package spd.trello.domain;


import lombok.*;
import spd.trello.domain.common.Domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table (name = "checkable_item")
public class CheckableItem extends Domain {
    @Column(name = "name")
    @NotNull(message = "The name field must be filled.")
    @Size(min = 2, max = 30, message = "The name field must be between 2 and 30 characters long.")
    private String name;
    @Column(name = "checked")
    private Boolean checked = Boolean.FALSE;
    @Column(name = "checklist_id")
    private UUID checklistId;;
}

