package za.co.photo_sharing.app_ws.config;

import za.co.photo_sharing.app_ws.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 3600000; // 1 hour
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users/create";
    public static final String USER_APP_REQUEST_URL = "/users_app_request/request-app-dev";
    public static final String USER_APP_REQ_EMAIL_VERIFY = "/users_app_request/request-app-email-verify";
    public static final String TOKEN_SECRET = "qFUch1xxu4jtKixVyLWGgxL0mPMGNHmRGl2oQUrI7FxtQYz5Dr5KGmGveVgyJve";
    public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
    public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
    public static final String PASSWORD_RESET_URL = "/users/password-reset";
    public static final String LOG_OUT_URL = "/users/logout";
    public static final String H2_CONSOLE = "/h2-console/**";
    public static final String AUTHORITIES_KEY = "scopes";
    public static final String IS_ADMIN = "admin";

    public static String getTokenSecret()
    {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }

}
