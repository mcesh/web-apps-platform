package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.UserAppRequest;
import za.co.photo_sharing.app_ws.entity.UserEntity;

public interface UserAppReqRepository extends JpaRepository<UserAppRequest,Long> {
    UserAppRequest findByEmail(String email);
    UserAppRequest findUserByEmailVerificationToken(String token);
}
