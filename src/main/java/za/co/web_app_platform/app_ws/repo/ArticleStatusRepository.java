package za.co.web_app_platform.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.web_app_platform.app_ws.entity.ArticleStatus;

public interface ArticleStatusRepository extends JpaRepository<ArticleStatus, Long> {
    ArticleStatus findByStatus(String status);
}
