package za.co.photo_sharing.app_ws.utility;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import za.co.photo_sharing.app_ws.entity.UserEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Transactional
public class UserIdFactoryImpl {


    @Autowired
    private SessionFactory sessionFactory;

    public Long buildUserId(){

        long generatedValue = System.nanoTime()/100;

        long userId;
        if (fetchUserIds().contains(generatedValue)){
            long incrementUserId = generatedValue;
            ++incrementUserId;
            userId = incrementUserId;
        }else {
            userId = generatedValue;
        }


        return userId;
    }

    public List<Long> fetchUserIds(){
        List<UserEntity> allUsers = getAllUsers();
       return allUsers.stream().map(UserEntity::getUserId).collect(Collectors.toList());
    }

    public List<UserEntity> getAllUsers(){
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(UserEntity.class);
        criteria.setFirstResult(0);
        criteria.setMaxResults(100);
        return criteria.list();
    }
}
