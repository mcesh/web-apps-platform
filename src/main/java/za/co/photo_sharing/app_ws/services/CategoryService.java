package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.entity.Category;

public interface CategoryService {

    Category findByUsernameAndCategoryName(String username, String name);
    Category save(String category, String username);
}
