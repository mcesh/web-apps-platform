package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import za.co.photo_sharing.app_ws.entity.Category;
import za.co.photo_sharing.app_ws.services.CategoryService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("category") // http://localhost:8080/comment/web-apps-platform
public class CategoryResource {

    private static Logger LOGGER = LoggerFactory.getLogger(CategoryResource.class);
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "Create Category Endpoint",
            notes = "${userResource.CreateCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}",
                    paramType = "header")
    })
    @PostMapping("/create/{categoryName}/{email}")
    public Category createCategory(@PathVariable String categoryName, @PathVariable String email) {

        getLog().info("Adding new category for.... {} ", email);
        Category category = categoryService.save(categoryName, email);
        getLog().info("Category created  {} ", category.getName());
        return category;

    }

    @ApiOperation(value="Get Categories Endpoint",
            notes="${userResource.GetCategories.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}",
                    paramType="header")
    })
    @GetMapping(path = "list/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Category> getCategories(@PathVariable String email){
        return categoryService.findAllCategoriesByEmail(email);

    }

    @ApiOperation(value="Get Category By Name Endpoint",
            notes="${userResource.GetCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}",
                    paramType="header")
    })
    @GetMapping(path = "/{email}/{categoryName}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Category getCategory(@PathVariable String email,
                                @PathVariable String categoryName){
        getLog().info("Getting Category for {} ", email);
        return categoryService.findByEmailAndCategoryName(email, categoryName);
    }

    @Secured("ROLE_ADMIN")
    @ApiOperation(value="Update Category Endpoint",
            notes="${userResource.UpdateCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}",
                    paramType="header")
    })
    @PutMapping(path = "/{id}/{categoryName}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Category updateCategory(@PathVariable Long id,
                                   @PathVariable String categoryName){
       return categoryService.updateCategory(id,categoryName);
    }

    @Secured("ROLE_ADMIN")
    @ApiOperation(value="Delete Category By ID Endpoint",
            notes="${userResource.DeleteCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}",
                    paramType="header")
    })
    @DeleteMapping(path = "/purge{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void deleteCategoryById(@PathVariable Long id){
        categoryService.deleteCategoryById(id);
    }

    @ApiOperation(value="Get Category By ID Endpoint",
            notes="${userResource.DeleteCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}",
                    paramType="header")
    })
    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Category getCategoryById(@PathVariable Long id){
        getLog().info("Getting category by ID {} at {}", id, LocalDateTime.now());
       return categoryService.findById(id);
    }


    public static Logger getLog() {
        return LOGGER;
    }
}
