package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.UserClient;

public interface UserClientRepository extends JpaRepository<UserClient,Long> {
    UserClient findByEmail(String email);
    UserClient findByClientID(String clientID);
}
