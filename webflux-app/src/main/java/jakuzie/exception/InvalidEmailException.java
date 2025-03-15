package jakuzie.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = BAD_REQUEST)
public class InvalidEmailException extends RuntimeException {

  public InvalidEmailException(String email) {
    super("Invalid email: %s".formatted(email));
  }

}
