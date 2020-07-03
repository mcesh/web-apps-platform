package za.co.photo_sharing.app_ws.model.response;

public enum ErrorMessages {

    MISSING_REQUIRED_FIELD("Missing required field. Please check documentation for required fields"),
    RECORD_ALREADY_EXISTS("Record already exists"),
    INTERNAL_SERVER_ERROR("Internal server error"),
    NO_RECORD_FOUND("Record with provided id is not found"),
    AUTHENTICATION_FAILED("Authentication failed"),
    COULD_NOT_UPDATE_RECORD("Could not update record"),
    COULD_NOT_DELETE_RECORD("Could not delete record"),
    USERNAME_ALREADY_EXISTS("Username already exists"),
    USER_NOT_FOUND("No user found with the specified userID,username or first name"),
    EMAIL_ADDRESS_ALREADY_EXISTS("Email address already exists"),
    NO_USERS_FOUND("No users found with the specified first name"),
    EMAIL_ADDRESS_NOT_VERIFIED("Email address could not be verified"),
    TOKEN_EXPIRED("Token has expired"),
    TOKEN_NOT_FOUND("Token value not found"),
    NUMBER_NOT_NUMERIC("Please provide digital numbers"),
    APP_TOKEN_NOT_FOUND("AppToken not found"),
    USER_NOT_AUTHORIZED("User not authorized to use provided appToken");


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
