package spd.trello.exception;

public class SecurityAccessException extends SecurityException {
    public SecurityAccessException() {
        super();
    }

    public SecurityAccessException(String s) {
        super(s);
    }
}