package za.co.web_app_platform.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.web_app_platform.app_ws.entity.ApplicationType;

public interface ApplicationTypeRepository extends JpaRepository<ApplicationType,Long> {
    ApplicationType findByAppTypeCode(String appTypeCode);
}
