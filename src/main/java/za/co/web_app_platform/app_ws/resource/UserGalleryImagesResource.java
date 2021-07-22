package za.co.web_app_platform.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.web_app_platform.app_ws.model.response.ImageGallery;
import za.co.web_app_platform.app_ws.model.response.OperationStatusModel;
import za.co.web_app_platform.app_ws.model.response.RequestOperationName;
import za.co.web_app_platform.app_ws.model.response.RequestOperationStatus;
import za.co.web_app_platform.app_ws.services.GalleryService;
import za.co.web_app_platform.app_ws.services.UserAppReqService;
import za.co.web_app_platform.app_ws.services.UserService;
import za.co.web_app_platform.app_ws.shared.dto.UserClientDTO;

import java.util.Set;

@RestController
@RequestMapping("api/gallery") // http://localhost:8080/users/web-apps-platform/api/gallery/
@Slf4j
public class UserGalleryImagesResource {

    @Autowired
    private UserService userService;
    @Autowired
    private GalleryService galleryService;
    @Autowired
    private UserAppReqService appReqService;

    private ModelMapper modelMapper = new ModelMapper();

    @ApiOperation(value = "The Upload User Gallery Images Endpoint",
            notes = "${userResource.GalleryImages.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "upload/gallery-image/{email}/{caption}/{categoryName}",
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel uploadImage(@PathVariable String email,
                                            @PathVariable String caption,
                                            @RequestParam("file") MultipartFile file,
                                            @PathVariable String categoryName) {
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.IMAGE_UPLOAD.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        log.info("Uploading Image for {}, caption is {} and category is {} ", email, caption, categoryName);
        userService.uploadUserGalleryImages(email, file, caption, categoryName);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return statusModel;
    }

    @ApiOperation(value = "The Download User Gallery Images Endpoint",
            notes = "${userResource.DownloadImages.ApiOperation.Notes}")
    @GetMapping(path = "download/gallery-images/{clientID}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public Set<ImageGallery> downloadGalleryImages(@PathVariable String clientID) {
        UserClientDTO clientDTO = appReqService.findByClientID(clientID);
        log.info("Retrieving a list of images... {} ", clientDTO.getEmail());
        Set<ImageGallery> galleryImages = userService.downloadUserGalleryImages(clientDTO.getEmail());
        log.info("Images retrieved {} ", galleryImages.size());
        return galleryImages;

    }

    @ApiOperation(value = "Fetch User Gallery Images Endpoint",
            notes = "${userResource.FetchGalleryImages.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}",
                    paramType = "header")
    })
    @GetMapping(path = "fetch/{clientID}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public Set<ImageGallery> fetchImages(@PathVariable String clientID) {
        UserClientDTO clientDTO = appReqService.findByClientID(clientID);
        log.info("Fetching a list of images... {} ", clientDTO.getEmail());
        Set<ImageGallery> galleryImages = userService.fetchGalleryImages(clientDTO.getEmail());
        log.info("Images retrieved {} ", galleryImages.size());
        return galleryImages;

    }

    @ApiOperation(value = "The Upload User Gallery Images Endpoint",
            notes = "${userResource.GalleryImages.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "upload/cloudinary/{email}/{caption}/{categoryName}",
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel uploadImageToCloudinary(@PathVariable String email,
                                                        @PathVariable String caption,
                                                        @RequestParam("file") MultipartFile file,
                                                        @PathVariable String categoryName) {
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.IMAGE_UPLOAD.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        log.info("Uploading Image for {} ", email);
        String uploadFile = galleryService.uploadGallery(email, file, caption, categoryName);
        log.info("Uploaded File: {} ", uploadFile);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return statusModel;
    }

    @ApiOperation(value = "Fetch User Gallery Images Endpoint",
            notes = "${userResource.PhotoDetails.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}",
                    paramType = "header")
    })
    @GetMapping(path = "photo/{clientID}/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public ImageGallery getPhotoDetails(@PathVariable String clientID,
                                             @PathVariable Long id) {
        UserClientDTO clientDTO = appReqService.findByClientID(clientID);
        log.info("Fetching photo details...{} ID...{} ", clientDTO.getEmail(), id);
        ImageGallery photoDetailsById = galleryService.getPhotoDetailsById(clientDTO.getEmail(), id);
        log.info("Photo Details: {}, ", photoDetailsById);
        return photoDetailsById;

    }

}
