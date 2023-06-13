package spd.trello.domain;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.common.Resource;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table (name = "checklist")
public class CheckList extends Resource {
    @Column(name = "name")
    @NotNull(message = "The name field must be filled.")
    @Size(min = 2, max = 30, message = "The name field must be between 2 and 30 characters long.")
    private String name;
    @Column(name = "card_id")
    private UUID cardId;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "checkable_item",
            joinColumns=@JoinColumn(name= "checklist_id")
    )
    @Column(name = "id")
    private Set<UUID> itemIds;
}
