package za.co.photo_sharing.app_ws.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class UserServiceException extends RuntimeException {

    private static final long serialVersionUID = 8965214323254522664L;

    private final HttpStatus status;
    private final String message;

    public UserServiceException(HttpStatus status, String message) {
        super();
        this.status = status;
        this.message = message;
    }


}
