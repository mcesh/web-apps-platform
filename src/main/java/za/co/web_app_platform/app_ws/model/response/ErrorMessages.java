package za.co.web_app_platform.app_ws.model.response;

public enum ErrorMessages {

    MISSING_REQUIRED_FIELD("Missing required field. Please check documentation for required fields"),
    RECORD_ALREADY_EXISTS("Record already exists"),
    INTERNAL_SERVER_ERROR("Internal server error"),
    NO_RECORD_FOUND("Record with provided id is not found"),
    AUTHENTICATION_FAILED("Authentication failed"),
    COULD_NOT_UPDATE_RECORD("Could not update record"),
    COULD_NOT_DELETE_RECORD("Could not delete record"),
    USERNAME_ALREADY_EXISTS("Username already exists"),
    USER_NOT_FOUND("No user found with the specified Email Address,userID,username or first name"),
    EMAIL_ADDRESS_ALREADY_EXISTS("Email address already exists"),
    NO_USERS_FOUND("No users found with the specified first name"),
    EMAIL_ADDRESS_NOT_VERIFIED("Email address could not be verified"),
    TOKEN_EXPIRED("Token has expired"),
    TOKEN_NOT_FOUND("Token value not found"),
    NUMBER_NOT_NUMERIC("Please provide digital numbers"),
    APP_TOKEN_NOT_FOUND("Token not found"),
    USER_NOT_AUTHORIZED("User not authorized to use provided appToken"),
    EMAIL_ADDRESS_NOT_FOUND("Email Address not found"),
    AUTHORITY_NOT_APPLICABLE("Requested Authority not Applicable"),
    ERROR_SENDING_EMAIL("An Exception occurred while sending mail"),
    ERROR_UPLOADING("Cannot Upload Empty file"),
    INCORRECT_IMAGE_FORMAT("File must me an image"),
    CATEGORY_NOT_FOUND("Category not found"),
    CATEGORY_ALREADY_EXISTS("Category already exists"),
    FILE_TOO_LARGE("File too large"),
    USER_NOT_VERIFIED("user email address not verified"),
    CLIENT_ID_ALREADY_DEFINED("Client ID already exists for this user"),
    CLIENT_INFORMATION_NOT_FOUND("Client information not found"),
    ARTICLE_STATUS_NOT_FOUND("Article status not found"),
    ARTICLE_NOT_FOUND("No article found with the specified ID"),
    NO_ARTICLES_FOUND_IN_RANGE("No articles found in the rage you provided"),
    COMMENT_NOT_FOUND("Comment not found"),
    ADDRESS_NOT_FOUND("No address found with the specified addressId"),
    ABOUT_PAGE_NOT_FOUND("About page not found"),
    EXCEEDED_IMAGE_LIMIT("exceeded image limit"),
    IMAGE_NOT_FOUND("Image not found"),
    IMAGE_TYPE_NOT_FOUND("Image type not found"),
    APP_TYPE_NOT_FOUND("Application type code not found"),
    SKILL_SET_NOT_FOUND("Skill set not found");


    private String errorMessage;

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
