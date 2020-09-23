package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.entity.Category;

import java.util.List;

public interface CategoryService {

    Category findByUsernameAndCategoryName(String username, String name);
    Category save(String category, String username);
    List<Category> findAllCategoriesByUsername(String email);
}
