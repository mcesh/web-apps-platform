package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.ApplicationType;

public interface ApplicationTypeRepository extends JpaRepository<ApplicationType,Long> {
    ApplicationType findByAppTypeCode(String appTypeCode);
}
