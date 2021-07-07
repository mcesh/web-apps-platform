package za.co.web_app_platform.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import za.co.web_app_platform.app_ws.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByName(String name);

    @Modifying
    @Transactional
    @Query("update Tag t set t.count =:count where t.name =:name")
    void updateArticleCount(@Param("count") int count, @Param("name") String name);

    /* @Query("SELECT name FROM UserProfile name where name.firstName =:firstName and name.userId =:userId")
    UserProfile findByFirstNameAndUserId(@Param("firstName") String firstName, @Param("userId") Long userId);*/
}
