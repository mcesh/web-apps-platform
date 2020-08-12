package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.model.response.OperationStatusModel;
import za.co.photo_sharing.app_ws.model.response.RequestOperationName;
import za.co.photo_sharing.app_ws.model.response.RequestOperationStatus;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.utility.EmailUtility;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/gallery") // http://localhost:8080/users/web-apps-platform
public class UserGalleryImagesResource {

    @Autowired
    private UserService userService;
    private ModelMapper modelMapper = new ModelMapper();
    private static Logger LOGGER = LoggerFactory.getLogger(UserAppRequestResource.class);

    @ApiOperation(value="The Upload User Gallery Images Endpoint",
            notes="${userResource.GalleryImages.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @PostMapping(path = "upload/gallery-image/{email}/{caption}",
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel uploadImage(@PathVariable String email,
                                            @PathVariable String caption,
                                            @RequestParam("file") MultipartFile file){
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.IMAGE_UPLOAD.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        userService.uploadUserGalleryImages(email,file, caption);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return statusModel;
    }

    @ApiOperation(value="The Download User Gallery Images Endpoint",
            notes="${userResource.DownImage.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @GetMapping(path = "download/gallery-image/{email}",
            produces = {MediaType.IMAGE_GIF_VALUE,
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_PNG_VALUE,
                    MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    MediaType.TEXT_PLAIN_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<byte[]>> downloadProfileImage(@PathVariable String email){
        HttpHeaders headers = new HttpHeaders();
        ArrayList<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.IMAGE_JPEG);
        acceptableMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        acceptableMediaTypes.add(MediaType.IMAGE_PNG    );


        getLog().info("Retrieving a list of images...");
        List<byte[]> bytes = userService.downloadUserGalleryImages(email);
        getLog().info("Images retrieved {} ", bytes.size());
        headers.setAccept(acceptableMediaTypes);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(bytes);
    }

    private MediaType contentType(String keyname) {
        String[] arr = keyname.split("\\.");
        String type = arr[arr.length-1];
        switch(type) {
            case "txt": return MediaType.TEXT_PLAIN;
            case "png": return MediaType.IMAGE_PNG;
            case "jpg": return MediaType.IMAGE_JPEG;
            default: return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public static Logger getLog() {
        return LOGGER;
    }
}
