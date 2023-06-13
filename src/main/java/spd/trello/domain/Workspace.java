package spd.trello.domain;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.common.Resource;
import spd.trello.domain.enums.WorkspaceVisibility;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "workspaces")
public class Workspace extends Resource {
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private WorkspaceVisibility visibility;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "member_workspace",
            joinColumns=@JoinColumn(name= "workspace_id")
    )
    @Column(name = "member_id")
    private Set<UUID> membersIds = new HashSet<>();

    public Workspace() {
    }

    public Workspace(Workspace entity) {
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.visibility = entity.getVisibility();
        this.membersIds = new HashSet<>(entity.getMembersIds());
    }
}

