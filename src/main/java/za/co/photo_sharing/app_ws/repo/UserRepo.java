package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.photo_sharing.app_ws.entity.UserProfile;

import java.util.List;

@Repository
@Transactional
public interface UserRepo extends JpaRepository<UserProfile, Long> {
    UserProfile findByEmail(String email);
    UserProfile findByUserId(Long userId);
    UserProfile findByUsername(String username);
    UserProfile findUserByEmailVerificationToken(String token);

    @Query("SELECT name FROM UserProfile name where name.firstName =:firstName and name.userId =:userId")
    UserProfile findByFirstNameAndUserId(@Param("firstName") String firstName, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE UserProfile WHERE id=:x")
    public void deleteUserByUserId(@Param("x") Long id);

    @Query(value="select name FROM UserProfile name where name.firstName = ?1")
    List<UserProfile> findUserByFirstName(@Param("firstName") String firstName);

    @Query(value = "select * from users u where u.emailVerificationStatus = 1",
            countQuery = "select count(*) from users u where u.emailVerificationStatus = 1",
            nativeQuery = true)
    Page<UserProfile> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);

    @Query(value="select * from users u where first_name LIKE %:keyword% or last_name LIKE %:keyword%",
            nativeQuery=true)
    List<UserProfile> findUsersByKeyword(@Param("keyword") String keyword);

    @Query(value="select u.first_name, u.last_name from users u where u.first_name LIKE %:keyword% or u.last_name LIKE %:keyword%",nativeQuery=true)
    List<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword);


}
