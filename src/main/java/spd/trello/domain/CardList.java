package spd.trello.domain;

import lombok.*;
import spd.trello.domain.common.Resource;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table
public class CardList extends Resource {
    @Column(name = "name")
    private String name;
    @Column(name = "archived")
    private Boolean archived = Boolean.FALSE;
    @Column(name = "index")
    private int index;
    @Column(name = "board_id")
    private UUID boardId;
    @Column(name = "description")
    private String description;



}

