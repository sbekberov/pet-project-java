package spd.trello.domain;
import lombok.Data;

@Data
public class AuthenticationRequestDTO {
    private String email;
    private String password;
}
