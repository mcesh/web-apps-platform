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
import za.co.web_app_platform.app_ws.model.response.ImageBucketRest;
import za.co.web_app_platform.app_ws.model.response.OperationStatusModel;
import za.co.web_app_platform.app_ws.model.response.RequestOperationName;
import za.co.web_app_platform.app_ws.model.response.RequestOperationStatus;
import za.co.web_app_platform.app_ws.services.ImageBucketService;
import za.co.web_app_platform.app_ws.services.UserAppReqService;
import za.co.web_app_platform.app_ws.shared.dto.ImageBucketDto;
import za.co.web_app_platform.app_ws.shared.dto.UserClientDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/bucket") // http://localhost:8080/users/web-apps-platform
@Slf4j
public class ImageBucketResource {

    @Autowired
    private ImageBucketService bucketService;
    @Autowired
    private UserAppReqService appReqService;
    private ModelMapper modelMapper = new ModelMapper();

    @ApiOperation(value = "Upload Image Endpoint",
            notes = "${userResource.BucketImages.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = {"upload/bucket/{username}","upload/bucket/{username}/{caption}"},
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel uploadImage(@PathVariable String username,
                                            @PathVariable(required = false) String caption,
                                            @RequestParam("file") MultipartFile file) throws IOException {
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.IMAGE_UPLOAD.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        log.info("Uploading Bucket image for {}, caption is {}", username, caption);
        bucketService.addImage(username, caption, file);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return statusModel;
    }

    @ApiOperation(value = "Upload Image and specify name Endpoint",
            notes = "${userResource.BucketImages.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = {"upload-with-name/bucket/{username}/{name}","upload-with-name/bucket/{username}/{caption}/{name}"},
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel uploadImageWithName(@PathVariable String username,
                                            @PathVariable(required = false) String caption,
                                            @RequestParam() String name,
                                            @RequestParam("file") MultipartFile file) throws IOException {
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.IMAGE_UPLOAD.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        log.info("Uploading Bucket image for {}, caption is {}", username, caption);
        bucketService.uploadImage(username, caption,name, file);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return statusModel;
    }

    @ApiOperation(value = "Download Image Endpoint",
            notes = "${userResource.ImagesByEmail.ApiOperation.Notes}")
    @GetMapping(path = "download/image/{clientID}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public List<ImageBucketRest> downloadImages(@PathVariable String clientID) {
        UserClientDTO clientDTO = appReqService.findByClientID(clientID);
        List<ImageBucketRest> imageBucketRests =  new ArrayList<>();
        log.info("Retrieving images... {} ", clientDTO.getEmail());
        List<ImageBucketDto> imageBucketDtos = bucketService.fetchImagesByEmail(clientDTO.getEmail());
        log.info("Images retrieved {} ", imageBucketDtos.size());
        imageBucketDtos.forEach(imageBucketDto -> {
            ImageBucketRest imageBucketRest = modelMapper.map(imageBucketDto, ImageBucketRest.class);
            imageBucketRests.add(imageBucketRest);
        });
        return imageBucketRests;

    }

    @ApiOperation(value = "View Image Endpoint",
            notes = "${userResource.SliderImageDetails.ApiOperation.Notes}")
    @GetMapping(path = "view/image-details/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public ImageBucketRest viewImageDetails(@PathVariable Long id) {
        ImageBucketDto bucketDto = bucketService.findById(id);
        return modelMapper.map(bucketDto, ImageBucketRest.class);
    }

    @ApiOperation(value = "Delete Images Endpoint",
            notes = "${userResource.DeleteImage.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "purge/slider-images/{username}/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public void deleteImade(@PathVariable String username,@PathVariable Long id) throws IOException {
        //ImageSliderDto sliderDto = sliderService.findById(id);
        bucketService.deleteImage(username, id);
    }

    @ApiOperation(value = "Update Slider Images Endpoint",
            notes = "${userResource.UpdateImageSlider.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PutMapping(path = {"/slider-images/{username}/{id}","/slider-images/{username}/{id}/{caption}"},
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public ImageBucketRest updateImage(@PathVariable String username,
                                       @PathVariable Long id,
                                       @RequestParam("file") MultipartFile file,
                                       @PathVariable(required = false) String caption) throws IOException {
        ImageBucketDto imageBucketDto = bucketService.updateImage(username, id, file, caption);
        return modelMapper.map(imageBucketDto, ImageBucketRest.class);
    }

    @ApiOperation(value = "Get Images By Name Endpoint",
            notes = "${userResource.ImagesByName.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "fetch-images/byName/{name}/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public List<ImageBucketRest> getImagesByName(@PathVariable String name, @PathVariable String email) {
        List<ImageBucketRest> imageBucketRests =  new ArrayList<>();
        log.info("Retrieving a images by name... {} ", email);
        List<ImageBucketDto> imageBucketDtos = bucketService.fetchImagesByName(name,email);
        log.info("Images retrieved {} ", imageBucketDtos.size());
        imageBucketDtos.forEach(imageBucketDto -> {
            ImageBucketRest imageBucketRest = modelMapper.map(imageBucketDto, ImageBucketRest.class);
            imageBucketRests.add(imageBucketRest);
        });
        return imageBucketRests;

    }
}
