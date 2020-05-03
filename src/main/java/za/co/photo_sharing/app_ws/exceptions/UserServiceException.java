package za.co.photo_sharing.app_ws.exceptions;

public class UserServiceException extends RuntimeException {

    private static final long serialVersionUID = 8965214323254522664L;

    public UserServiceException(String message){
        super(message);
    }


}
