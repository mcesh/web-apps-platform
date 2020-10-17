package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.Article;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findById(Long id);
    Page<Article> findByEmail(String email, Pageable pageable);
}
