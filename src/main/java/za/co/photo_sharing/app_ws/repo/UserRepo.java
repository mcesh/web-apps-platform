package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.photo_sharing.app_ws.entity.UserEntity;

import java.util.List;

@Repository
@Transactional
public interface UserRepo extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUserId(Long userId);
    UserEntity findByUsername(String username);
    UserEntity findUserByEmailVerificationToken(String token);

    @Query("SELECT name FROM UserEntity name where name.firstName =:firstName and name.userId =:userId")
    UserEntity findByFirstNameAndUserId(@Param("firstName") String firstName, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE UserEntity WHERE id=:x")
    public void deleteUserByUserId(@Param("x") Long id);

    @Query(value="select name FROM UserEntity name where name.firstName = ?1")
    List<UserEntity> findUserByFirstName(@Param("firstName") String firstName);

    @Query(value = "select * from Users u where u.emailVerificationStatus = 1",
            countQuery = "select count(*) from Users u where u.emailVerificationStatus = 1",
            nativeQuery = true)
    Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);


}
