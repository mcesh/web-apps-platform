package za.co.web_app_platform.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.web_app_platform.app_ws.entity.UserAppRequest;

public interface UserAppReqRepository extends JpaRepository<UserAppRequest,Long> {
    UserAppRequest findByEmail(String email);
    UserAppRequest findUserByEmailVerificationToken(String token);
    UserAppRequest findByOrganizationUsername(String OrganizationUsername);
}
