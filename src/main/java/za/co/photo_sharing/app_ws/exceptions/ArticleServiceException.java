package za.co.photo_sharing.app_ws.exceptions;

public class ArticleServiceException extends RuntimeException {

    private static final long serialVersionUID = 7542145222151101401L;

    public ArticleServiceException(String message){
        super(message);
    }
}
