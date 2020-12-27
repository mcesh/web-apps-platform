package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.ImageSlider;

import java.util.List;

public interface ImageSliderRepository extends JpaRepository<ImageSlider, Long> {
    List<ImageSlider> findByEmail(String email);
}
