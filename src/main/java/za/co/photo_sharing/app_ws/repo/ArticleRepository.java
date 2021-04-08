package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.photo_sharing.app_ws.entity.Article;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import za.co.photo_sharing.app_ws.entity.Comment;

import javax.transaction.Transactional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findById(Long id);
    Page<Article> findByEmail(String email, Pageable pageable);
    Page<Article> findByStatus(String status, Pageable pageable);
    @Query(value = "select status from Article status where status.status = status and status.email = email")
    Page<Article> findArticlesByStatus(@Param("email") String email, @Param("status") String status, Pageable pageableRequest);
    List<Article> findByTitleContaining(String title);
    @Query(value = "select artcle.*, count(*) as article_count from article artcle" +
            " " +
            "INNER JOIN article_comments cmnts ON cmnts.article_id = artcle.id" +
            " " +
            "WHERE artcle.email =:email" +
            " " +
            "GROUP BY artcle.id " +
            "HAVING article_count > 0", nativeQuery = true)
    List<Article> getFamousArticles(@Param("email") String email);
}
