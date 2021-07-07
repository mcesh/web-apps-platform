package za.co.web_app_platform.app_ws.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import za.co.web_app_platform.app_ws.entity.Category;
import za.co.web_app_platform.app_ws.exceptions.UserServiceException;
import za.co.web_app_platform.app_ws.model.response.ErrorMessages;
import za.co.web_app_platform.app_ws.repo.CategoryRepository;
import za.co.web_app_platform.app_ws.services.CategoryService;
import za.co.web_app_platform.app_ws.services.UserService;
import za.co.web_app_platform.app_ws.shared.dto.UserDto;
import za.co.web_app_platform.app_ws.utility.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final Utils utils;

    @Transactional
    @Override
    public Category save(String categoryName, String email) {
        Category category_ = categoryRepository.findByEmailAndCategoryName(email, categoryName);
        if (Objects.nonNull(category_)) {
            throw new UserServiceException(HttpStatus.BAD_REQUEST, ErrorMessages.CATEGORY_ALREADY_EXISTS.getErrorMessage());
        }
        Category category = new Category();
        category.setName(categoryName.trim());
        category.setEmail(userService.findByEmail(email).getEmail());
        category.setArticleCount(0);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created for {} ", savedCategory.getEmail());
        return savedCategory;
    }

    @Transactional
    @Override
    public List<Category> findAllCategoriesByEmail(String email) {
        UserDto userServiceByEmail = userService.findByEmail(email);
        if (Objects.isNull(userServiceByEmail)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        List<Category> categories = categoryRepository.findAllCategoriesByEmail(email);
        if (!CollectionUtils.isEmpty(categories)) {
            categories.forEach(category -> {
                log.info("Category Found {} ", category.getName());
            });
        }
        log.info("Categories found: {} ", categories.size());
        return categories;
    }

    @Transactional
    @Override
    public void updateArticleCount(int count, String categoryName, String email) {
        categoryRepository.updateArticleCount(count, categoryName, email);
    }

    @Transactional
    @Override
    public Category updateCategory(Long id, String categoryName) {
        Optional<Category> category = getCategory(id);
        category.get().setName(categoryName);
        log.info("Category {} ", category);
        categoryRepository.save(category.get());
        return category.get();
    }

    @Transactional
    @Override
    public Category findById(Long id) {
        Optional<Category> category = getCategory(id);
        log.info("Category by id{} ", category);
        return category.get();
    }

    private Optional<Category> getCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (!category.isPresent()) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        return category;
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long id) {
        Optional<Category> category = getCategory(id);
        log.info("Deleting category with name {} ", category.get().getName());
        categoryRepository.flush();
        categoryRepository.delete(category.get());
    }

    @Transactional
    @Override
    public List<Category> findAllCategories(int page, int size) {
        Utils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();
        log.info("Categories Found in DB: {} ", categories.size());
        if (CollectionUtils.isEmpty(categories)) {
            return new ArrayList<>();
        }
        return categories;
    }

    @Transactional
    @Override
    public Category findByEmailAndCategoryName(String email, String name) {
        Category category = categoryRepository.findByEmailAndCategoryName(email, name);
        if (Objects.isNull(category)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        log.info("Category Found {} ", category);
        return category;
    }
}
