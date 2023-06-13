package spd.trello.domain;


import lombok.Getter;
import lombok.Setter;
import spd.trello.domain.common.Resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "attachment")
public class Attachment extends Resource {
    @Column(name = "link")
    private String link;
    @Column(name = "name")
    @NotNull(message = "The name field must be filled.")
    @Size(min = 2, max = 30, message = "The name field must be between 2 and 30 characters long.")
    private String name;
    @Column(name = "card_id")
    private UUID cardId;
    @Column(name = "multi_part_bytes")
    private byte[] multiPartBytes;
}
