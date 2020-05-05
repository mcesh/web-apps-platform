package za.co.photo_sharing.app_ws.utility;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.co.photo_sharing.app_ws.entity.UserEntity;

import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
@Transactional
public class UserIdFactory {

    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_NUMBERS = NUMBER;
    private static SecureRandom random = new SecureRandom();


    @Autowired
    private SessionFactory sessionFactory;

    public Long buildUserId() {

        long generatedValue = Long.parseLong(generateRandomNumbers(7));

        long userId;
        if (existingUserIds().contains(generatedValue)) {
            userId = generatedValue + 1;
        }else {
            userId = generatedValue;
        }


        return userId;
    }

    public List<Long> existingUserIds() {
        List<UserEntity> allUsers = getAllUsers();
        return allUsers.stream().map(UserEntity::getUserId).collect(Collectors.toList());
    }

    public List<UserEntity> getAllUsers() {
        String users = "from UserEntity";
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createQuery(users);
        query.setFirstResult(0);
        query.setMaxResults(100);
        return query.getResultList();
    }

    public String generateRandomNumbers(int length) {

        String characterType = DATA_FOR_RANDOM_NUMBERS;

        if (length < 1) throw new IllegalArgumentException();

        StringBuilder builder = new StringBuilder();
        IntStream.range(0,length).forEach(value -> {
            int randomCharaters = random.nextInt(characterType.length());
            char character = characterType.charAt(randomCharaters);
            builder.append(character);
        });
        builder.append(1);
        return builder.toString();
    }

}
