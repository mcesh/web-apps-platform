package za.co.web_app_platform.app_ws.utility;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


@Component
@Transactional
@Slf4j
public class UserIdFactory {

    @Autowired
    private SessionFactory sessionFactory;

    public Long buildUserId() {
        return Long.parseLong(Objects.requireNonNull(generateRandomNumbers()));
    }

    private String generateRandomNumbers() {

        try {

            ZonedDateTime zonedDateTime = ZonedDateTime.now();
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss Z");
            String formattedString2 = zonedDateTime.format(formatter2);
            String modifiedDate = formattedString2.replace("/", "")
                    .replace("-", "").replace(":", "").substring(0, 17).trim();
            return modifiedDate.replace(" ", "").substring(2);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
