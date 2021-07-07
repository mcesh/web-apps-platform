package za.co.web_app_platform.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.web_app_platform.app_ws.entity.PasswordResetToken;

@Repository
@Transactional
public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

}
