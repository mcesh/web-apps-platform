package za.co.photo_sharing.app_ws.exceptions;

public class InvalidTagException extends RuntimeException {

    private static final long serialVersionUID = 8965214323254522664L;

    public InvalidTagException(String errorMessage){
        super(errorMessage);
    }
}
