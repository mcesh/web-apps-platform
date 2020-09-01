package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.entity.Category;
import za.co.photo_sharing.app_ws.model.response.*;
import za.co.photo_sharing.app_ws.services.CategoryService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.EmailUtility;

import java.util.Set;

@RestController
@RequestMapping("api/gallery") // http://localhost:8080/users/web-apps-platform

public class UserGalleryImagesResource {

    @Autowired
    private  UserService userService;
    @Autowired
    private  CategoryService categoryService;
    private ModelMapper modelMapper = new ModelMapper();
    private static Logger LOGGER = LoggerFactory.getLogger(UserGalleryImagesResource.class);

    @ApiOperation(value="The Upload User Gallery Images Endpoint",
            notes="${userResource.GalleryImages.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @PostMapping(path = "upload/gallery-image/{email}/{caption}/{categoryName}",
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel uploadImage(@PathVariable String email,
                                            @PathVariable String caption,
                                            @RequestParam("file") MultipartFile file,
                                            @PathVariable String categoryName){
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.IMAGE_UPLOAD.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        getLog().info("Uploading Image for {}, caption is {} and category is {} " , email, caption, categoryName);
        userService.uploadUserGalleryImages(email,file, caption, categoryName);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return statusModel;
    }

    @ApiOperation(value="The Download User Gallery Images Endpoint",
            notes="${userResource.DownloadImages.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}",
                    paramType="header")
    })
    @GetMapping(path = "download/gallery-images/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
     public Set<ImageGallery> downloadGalleryImages(@PathVariable String email){

        getLog().info("Retrieving a list of images... {} ", email);
        Set<ImageGallery> galleryImages = userService.downloadUserGalleryImages(email);
        getLog().info("Images retrieved {} ", galleryImages.size());
        return galleryImages;

    }

    @ApiOperation(value="The Created Category Endpoint",
            notes="${userResource.CreateCategory.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}",
                    paramType="header")
    })
    @PostMapping(path = "category/new-category/{categoryName}/{username}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Category createCategory(@PathVariable String categoryName, String username){

        getLog().info("Adding new category....");
        Category category = categoryService.save(categoryName, username);
        getLog().info("Category created  {} ", category.getName());
        return category;

    }

    public static Logger getLog() {
        return LOGGER;
    }
}
