package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.photo_sharing.app_ws.entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    UserEntity findUserByEmailVerificationToken(String token);


}
