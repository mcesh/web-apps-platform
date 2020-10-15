package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
