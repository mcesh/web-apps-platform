package za.co.photo_sharing.app_ws.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = -6593330219878485669L;

    private final HttpStatus status;
    private final String message;

    public ValidationException(HttpStatus status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

}
