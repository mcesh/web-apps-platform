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
    public static final String ABOUT_PAGE_IMAGE = "/about/download/about-page/**";
    public static final String SLIDER_IMAGES = "/api/slider/download/slider-images/**";
    public static final String IMAGE_GALLERY = "/api/gallery/download/gallery-images/**";
    public static final String VIEW_IMAGE_DETAILS = "/api/gallery/photo/**";
    public static final String GET_ABOUT_DETAILS = "/about/getBy/**";
    public static final String GET_APP_TYPES = "/users_app_request/app_type/**";
    public static final String GET_ARTICLES_BY_EMAIL = "/article/all/email/**";
    public static final String GET_LATEST_ARTICLES_BY_EMAIL = "/article/latest/email/**";
    public static final String GET_CATEGORIES_BY_EMAIL = "/category/list-by-clientID/**";
    public static final String LOG_OUT_URL = "/users/logout";
    public static final String H2_CONSOLE = "/h2-console/**";
    public static final String AUTHORITIES_KEY = "scopes";
    public static final String IS_ADMIN = "admin";
    public static final String USERNAME = "userName";
    public static final String NAME = "name";
    public static final int MAX_SLIDER_IMAGES = 9;
    public static final int MAX_SERVICES_IMAGES = 6;
    public static final int MAX_PROJECTS_IMAGES = 6;
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "30";
    public static final int MAX_PAGE_SIZE = 30;

    public static String getTokenSecret()
    {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }

}
