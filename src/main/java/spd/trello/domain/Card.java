package spd.trello.domain;


import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.common.Resource;
import spd.trello.domain.enums.DifficultyLevel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "card")
public class Card extends Resource {
    @Column(name = "name")
    @NotNull(message = "The name field must be filled.")
    @Size(min = 2, max = 30, message = "The name field must be between 2 and 30 characters long.")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "index")
    private int index;
    @Column(name = "archived")
    private Boolean archived = Boolean.FALSE;
    @Column(name = "card_list_id")
    private UUID cardListId;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private DifficultyLevel difficulty;
    @Column(name = "estimated_time")
    private int estimatedTimeForToBeDone;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "reminder_id")
    private Reminder reminder;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "attachment",
            joinColumns=@JoinColumn(name= "card_id")
    )
    @Column(name = "id")
    private Set<UUID> attachmentIds;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "checklist",
            joinColumns=@JoinColumn(name= "card_id")
    )
    @Column(name = "id")
    private Set<UUID> checklists;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "label",
            joinColumns=@JoinColumn(name= "card_id")
    )
    @Column(name = "id")
    private Set<UUID> labels = new HashSet<>();

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "comment",
            joinColumns=@JoinColumn(name= "card_id")
    )
    @Column(name = "id")
    private Set<UUID> comments;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "card_member",
            joinColumns = @JoinColumn(name = "card_id")
    )
    @Column(name = "member_id")
    private Set<UUID> membersIds;


}

