package za.co.web_app_platform.app_ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {
    @Autowired
    private Environment env;

    public String getTokenSecret()
    {
        return env.getProperty("tokenSecret");
    }
}
