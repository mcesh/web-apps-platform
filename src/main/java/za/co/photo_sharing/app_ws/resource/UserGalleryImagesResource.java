package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserClientDTO;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/gallery") // http://localhost:8080/users/web-apps-platform

public class UserGalleryImagesResource {

    @Autowired
    private  UserService userService;
    @Autowired
    private UserAppReqService appReqService;

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
    @GetMapping(path = "download/gallery-images/{clientID}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
     public Set<ImageGallery> downloadGalleryImages(@PathVariable String clientID){
        UserClientDTO clientDTO = appReqService.findByClientID(clientID);
        getLog().info("Retrieving a list of images... {} ", clientDTO.getEmail());
        Set<ImageGallery> galleryImages = userService.downloadUserGalleryImages(clientDTO.getEmail());
        getLog().info("Images retrieved {} ", galleryImages.size());
        return galleryImages;

    }

    @ApiOperation(value="Fetch User Gallery Images Endpoint",
            notes="${userResource.FetchGalleryImages.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}",
                    paramType="header")
    })
    @GetMapping(path = "fetch/{clientID}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public Set<ImageGallery> fetchImages(@PathVariable String clientID){
        UserClientDTO clientDTO = appReqService.findByClientID(clientID);
        getLog().info("Fetching a list of images... {} ", clientDTO.getEmail());
        Set<ImageGallery> galleryImages = userService.fetchGalleryImages(clientDTO.getEmail());
        getLog().info("Images retrieved {} ", galleryImages.size());
        return galleryImages;

    }

    public static Logger getLog() {
        return LOGGER;
    }
}
