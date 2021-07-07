package za.co.web_app_platform.app_ws.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class ArticleServiceException extends RuntimeException {

    private static final long serialVersionUID = 7542145222151101401L;

    private final HttpStatus status;
    private final String message;

    public ArticleServiceException(HttpStatus status, String message) {
        super();
        this.status = status;
        this.message = message;
    }
}
