package za.co.photo_sharing.app_ws.utility;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.co.photo_sharing.app_ws.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;


@Component
@Transactional
public class UserIdFactoryImpl {


    @Autowired
    private SessionFactory sessionFactory;

    public Long buildUserId() {

        long generatedValue = System.nanoTime() / 100000;

        long userId;
        if (existingUserIds().contains(generatedValue)) {
            long incrementUserId = generatedValue;
            ++incrementUserId;
            userId = incrementUserId;
        } else {
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
}
