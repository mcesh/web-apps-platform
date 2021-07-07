package za.co.web_app_platform.app_ws.services;

import za.co.web_app_platform.app_ws.entity.Category;

import java.util.List;

public interface CategoryService {

    Category findByEmailAndCategoryName(String email, String name);
    Category save(String category, String username);
    List<Category> findAllCategoriesByEmail(String email);
    void updateArticleCount(int count,String categoryName, String email);
    Category updateCategory(Long id,String categoryName);
    Category findById(Long id);
    void deleteCategoryById(Long id);
    List<Category> findAllCategories(int page, int size);
}
