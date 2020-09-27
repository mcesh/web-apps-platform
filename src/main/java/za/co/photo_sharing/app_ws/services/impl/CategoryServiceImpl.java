package za.co.photo_sharing.app_ws.services.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import za.co.photo_sharing.app_ws.entity.Category;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.CategoryRepository;
import za.co.photo_sharing.app_ws.services.CategoryService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final Utils utils;
    private static Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Transactional
    @Override
    public Category save(String categoryName, String email) {
        Category category_ = categoryRepository.findByEmailAndCategoryName(email, categoryName);
        if (Objects.nonNull(category_)){
            throw new UserServiceException(ErrorMessages.CATEGORY_ALREADY_EXISTS.getErrorMessage());
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setEmail(userService.findByEmail(email).getEmail());
        Category savedCategory = categoryRepository.save(category);
        getLog().info("Category created for {} ", savedCategory.getEmail());
        return savedCategory;
    }

    @Override
    public List<Category> findAllCategoriesByEmail(String email) {
        UserDto userServiceByEmail = userService.findByEmail(email);
        if (Objects.isNull(userServiceByEmail)){
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        List<Category> categories = categoryRepository.findAllCategoriesByEmail(email);
        if (!CollectionUtils.isEmpty(categories)){
            categories.forEach(category -> {
                getLog().info("Category Found {} ", category.getName());
            });
        }
        return categories;
    }

    @Transactional
    @Override
    public Category findByEmailAndCategoryName(String email, String name) {
        Category category = categoryRepository.findByEmailAndCategoryName(email, name);
        if (Objects.isNull(category)){
            throw new UserServiceException(ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        return category;
    }

    public static Logger getLog() {
        return LOGGER;
    }
}
