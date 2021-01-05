package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.ImageBucket;

import java.util.List;

public interface ImageSliderRepository extends JpaRepository<ImageBucket, Long> {
    List<ImageBucket> findByEmail(String email);
}
