package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.entity.Category;

import java.util.List;

public interface CategoryService {

    Category findByEmailAndCategoryName(String email, String name);
    Category save(String category, String username);
    List<Category> findAllCategoriesByEmail(String email);
    void updateArticleCount(int count,String categoryName, String email);
}
