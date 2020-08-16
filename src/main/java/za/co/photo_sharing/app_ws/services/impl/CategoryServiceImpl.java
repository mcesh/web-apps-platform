package za.co.photo_sharing.app_ws.services.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.photo_sharing.app_ws.entity.Category;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.CategoryRepository;
import za.co.photo_sharing.app_ws.services.CategoryService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

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
    public Category save(String categoryName, String username) {
        Category category_ = categoryRepository.findByUsernameAndCategoryName(username, categoryName);
        if (Objects.nonNull(category_)){
            throw new UserServiceException(ErrorMessages.CATEGORY_ALREADY_EXISTS.getErrorMessage());
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setUsername(userService.findByUsername(username).getUsername());
        Category savedCategory = categoryRepository.save(category);
        getLog().info("Category created for {} ", savedCategory.getUsername());
        return savedCategory;
    }

    @Transactional
    @Override
    public Category findByUsernameAndCategoryName(String username, String name) {
        Category category = categoryRepository.findByUsernameAndCategoryName(username, name);
        if (Objects.isNull(category)){
            throw new UserServiceException(ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        return category;
    }

    public static Logger getLog() {
        return LOGGER;
    }
}
