package spd.trello.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class AlgorithmDTO {
    private UUID cardListIdBacklog;
    private UUID cardListIdToDo;
}
