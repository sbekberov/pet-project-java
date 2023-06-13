package spd.trello.domain;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.common.Resource;
import spd.trello.domain.enums.BoardVisibility;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table
public class Board extends Resource {
    @Column(name = "name")
    @NotNull(message = "The name field must be filled.")
    @Size(min = 2, max = 30, message = "The name field must be between 2 and 30 characters long.")
    private String name;
    @Column(name = "description")
    @Size(min = 2, max = 255, message = "The description field must be between 2 and 255 characters long.")
    private String description;
    @Column(name = "archived")
    private Boolean archived = Boolean.FALSE;
    @Column(name = "workspace_id")
    private UUID workspaceId;
    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private BoardVisibility visibility;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "board_member",
            joinColumns=@JoinColumn(name= "board_id")
    )
    @Column(name = "member_id")
    private Set<UUID> membersIds = new HashSet<>();



    public Board() {
    }

    public Board(Board entity) {
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.visibility = entity.getVisibility();
        this.membersIds = new HashSet<>(entity.getMembersIds());
    }
}
