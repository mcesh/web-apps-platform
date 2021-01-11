package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.ImageType;

public interface ImageTypeRepository extends JpaRepository<ImageType, Long> {

    ImageType findByCode(String code);

}
