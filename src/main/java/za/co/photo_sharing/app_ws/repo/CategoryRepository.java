package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import za.co.photo_sharing.app_ws.entity.Category;

import java.util.List;

public interface CategoryRepository  extends JpaRepository<Category, Long> {
    Category findByName(String name);
    @Query(value = "select category from Category category where category.email =:email and category.name =:name")
    Category findByEmailAndCategoryName(@Param("email") String email, @Param("name") String name);

    @Query(value = "select category from Category category where category.email =:email")
    List<Category> findAllCategoriesByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "update category c set c.articleCount =:count where c.email =:email and c.name=:name", nativeQuery = true)
    void updateArticleCount(@Param("count") int count,@Param("name") String name, @Param("email") String email);
}
