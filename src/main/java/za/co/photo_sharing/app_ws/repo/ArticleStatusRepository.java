package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.ArticleStatus;

public interface ArticleStatusRepository extends JpaRepository<ArticleStatus, Long> {
    ArticleStatus findByStatus(String status);
}
