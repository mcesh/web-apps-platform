package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.co.photo_sharing.app_ws.entity.Category;

public interface CategoryRepository  extends JpaRepository<Category, Long> {
    Category findByName(String name);
    @Query(value = "select category from Category category where category.username =:username and category.name =:name")
    Category findByUsernameAndCategoryName(@Param("username") String username, @Param("name") String name);
}
