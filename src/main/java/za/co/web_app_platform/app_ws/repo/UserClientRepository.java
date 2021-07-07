package za.co.web_app_platform.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.web_app_platform.app_ws.entity.UserClient;

public interface UserClientRepository extends JpaRepository<UserClient,Long> {
    UserClient findByEmail(String email);
    UserClient findByClientID(String clientID);
}
