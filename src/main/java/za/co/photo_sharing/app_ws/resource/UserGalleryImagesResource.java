package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.model.response.OperationStatusModel;
import za.co.photo_sharing.app_ws.model.response.RequestOperationName;
import za.co.photo_sharing.app_ws.model.response.RequestOperationStatus;
import za.co.photo_sharing.app_ws.model.response.UserRest;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

@RestController
@RequestMapping("api/gallery") // http://localhost:8080/users/web-apps-platform
public class UserGalleryImagesResource {

    @Autowired
    private UserService userService;
    private ModelMapper modelMapper = new ModelMapper();

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
}
