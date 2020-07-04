package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.AppToken;

public interface AppTokenRepository extends JpaRepository<AppToken,Long> {
    AppToken findByTokenKey(String tokenKey);
}
