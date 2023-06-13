package spd.trello.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="File can`t be upload")
public class FileCanNotBeUpload extends RuntimeException {
}

