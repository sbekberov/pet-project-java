package spd.trello.domain.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@NoArgsConstructor
@MappedSuperclass
public class Domain {
    @Id
    UUID id = UUID.randomUUID();

}
