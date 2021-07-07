package za.co.web_app_platform.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import za.co.web_app_platform.app_ws.config.SecurityConstants;
import za.co.web_app_platform.app_ws.entity.Category;
import za.co.web_app_platform.app_ws.services.CategoryService;
import za.co.web_app_platform.app_ws.services.UserAppReqService;
import za.co.web_app_platform.app_ws.shared.dto.UserClientDTO;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("category") // http://localhost:8080/comment/web-apps-platform
@Slf4j
public class CategoryResource {

    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserAppReqService appReqService;

    @ApiOperation(value = "Create Category Endpoint",
            notes = "${userResource.CreateCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}",
                    paramType = "header")
    })
    @PostMapping("/create/{categoryName}/{email}")
    public Category createCategory(@PathVariable String categoryName, @PathVariable String email) {

        log.info("Adding new category for.... {} ", email);
        Category category = categoryService.save(categoryName, email);
        log.info("Category created  {} ", category.getName());
        return category;

    }

    @ApiOperation(value = "Get Categories Endpoint",
            notes = "${userResource.GetCategories.ApiOperation.Notes}")
    @GetMapping(path = "list-by-clientID/{clientID}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Category> getCategoriesByEmail(@PathVariable String clientID) {
        UserClientDTO clientDTO = appReqService.findByClientID(clientID);
        log.info("Getting categories for: {} time: {}", clientDTO.getEmail(), LocalDateTime.now());
        return categoryService.findAllCategoriesByEmail(clientDTO.getEmail());

    }

    @ApiOperation(value = "Get Category By Name Endpoint",
            notes = "${userResource.GetCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}",
                    paramType = "header")
    })
    @GetMapping(path = "/{email}/{categoryName}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Category getCategory(@PathVariable String email,
                                @PathVariable String categoryName) {
        log.info("Getting Category for {} ", email);
        return categoryService.findByEmailAndCategoryName(email, categoryName);
    }

    @Secured("ROLE_ADMIN")
    @ApiOperation(value = "Update Category Endpoint",
            notes = "${userResource.UpdateCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}",
                    paramType = "header")
    })
    @PutMapping(path = "/{id}/{categoryName}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Category updateCategory(@PathVariable Long id,
                                   @PathVariable String categoryName) {
        return categoryService.updateCategory(id, categoryName);
    }

    @Secured("ROLE_ADMIN")
    @ApiOperation(value = "Delete Category By ID Endpoint",
            notes = "${userResource.DeleteCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}",
                    paramType = "header")
    })
    @DeleteMapping(path = "/purge{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
    }

    @ApiOperation(value = "Get Category By ID Endpoint",
            notes = "${userResource.DeleteCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}",
                    paramType = "header")
    })
    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Category getCategoryById(@PathVariable Long id) {
        log.info("Getting category by ID {} at {}", id, LocalDateTime.now());
        return categoryService.findById(id);
    }

    @ApiOperation(value = "Get Categories Endpoint",
            notes = "${userResource.GetAllCategories.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}",
                    paramType = "header")
    })
    @GetMapping(path = "/list",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Category> getAllCategories(@RequestParam(value = "page", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                           @RequestParam(value = "size", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return categoryService.findAllCategories(page, size);

    }
}
