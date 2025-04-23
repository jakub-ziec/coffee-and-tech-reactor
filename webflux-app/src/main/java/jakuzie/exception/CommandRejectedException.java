package jakuzie.exception;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = UNPROCESSABLE_ENTITY)
public class CommandRejectedException extends RuntimeException {

  public CommandRejectedException() {
    super("Command rejected");
  }

}
