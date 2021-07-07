package za.co.web_app_platform.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.web_app_platform.app_ws.entity.ImageBucket;

import java.util.List;

public interface ImageBucketRepository extends JpaRepository<ImageBucket, Long> {
    List<ImageBucket> findByEmail(String email);
    //List<Article> findByTitleContaining(String title);
    List<ImageBucket> findByNameContaining(String name);
}
