package jakuzie.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.NoSuchElementException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = NOT_FOUND)
public class ResourceNotFoundException extends NoSuchElementException {

}
