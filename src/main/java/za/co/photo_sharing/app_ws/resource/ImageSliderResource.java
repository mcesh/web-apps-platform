package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.model.response.*;
import za.co.photo_sharing.app_ws.services.ImageSliderService;
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.shared.dto.ImageSliderDto;
import za.co.photo_sharing.app_ws.shared.dto.UserClientDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/slider") // http://localhost:8080/users/web-apps-platform
@Slf4j
public class ImageSliderResource {

    @Autowired
    private ImageSliderService sliderService;
    @Autowired
    private UserAppReqService appReqService;
    private ModelMapper modelMapper = new ModelMapper();

    @ApiOperation(value = "Upload Slider Images Endpoint",
            notes = "${userResource.SliderImages.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "upload/slider/{username}/{caption}",
            produces = {MediaType.MULTIPART_FORM_DATA_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel uploadImage(@PathVariable String username,
                                            @PathVariable(required = false) String caption,
                                            @RequestParam("file") MultipartFile file) throws IOException {
        OperationStatusModel statusModel = new OperationStatusModel();
        statusModel.setOperationName(RequestOperationName.IMAGE_UPLOAD.name());
        statusModel.setOperationResult(RequestOperationStatus.ERROR.name());
        log.info("Uploading Slider image for {}, caption is {}", username, caption);
        sliderService.addImage(username, caption, file);
        statusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return statusModel;
    }

    @ApiOperation(value = "Download Slider Images Endpoint",
            notes = "${userResource.SliderImagesByEmail.ApiOperation.Notes}")
    @GetMapping(path = "download/slider-images/{clientID}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public List<ImageSliderRest> downloadSliderImages(@PathVariable String clientID) {
        UserClientDTO clientDTO = appReqService.findByClientID(clientID);
        List<ImageSliderRest> sliderRests =  new ArrayList<>();
        log.info("Retrieving a slider images... {} ", clientDTO.getEmail());
        List<ImageSliderDto> imageSliderDtos = sliderService.fetchImagesByEmail(clientDTO.getEmail());
        log.info("Images retrieved {} ", imageSliderDtos.size());
        imageSliderDtos.forEach(imageSliderDto -> {
            ImageSliderRest imageSliderRest = modelMapper.map(imageSliderDto, ImageSliderRest.class);
            sliderRests.add(imageSliderRest);
        });
        return sliderRests;

    }

    @ApiOperation(value = "View Slider Images Endpoint",
            notes = "${userResource.SliderImageDetails.ApiOperation.Notes}")
    @GetMapping(path = "view/slider-images/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public ImageSliderRest viewSliderImageDetails(@PathVariable Long id) {
        ImageSliderDto sliderDto = sliderService.findById(id);
        return modelMapper.map(sliderDto, ImageSliderRest.class);
    }

    @ApiOperation(value = "Delete Slider Images Endpoint",
            notes = "${userResource.DeleteImageSlider.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "purge/slider-images/{username}/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public void deleteImade(@PathVariable String username,@PathVariable Long id) throws IOException {
        //ImageSliderDto sliderDto = sliderService.findById(id);
        sliderService.deleteImage(username, id);
    }

    @ApiOperation(value = "Update Slider Images Endpoint",
            notes = "${userResource.UpdateImageSlider.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PutMapping(path = "/slider-images/{username}/{id}/{caption}",
            produces = {MediaType.APPLICATION_JSON_VALUE,})
    public ImageSliderRest updateImage(@PathVariable String username,
                                       @PathVariable Long id,
                                       @RequestParam("file") MultipartFile file,
                                       @PathVariable String caption) throws IOException {
        ImageSliderDto imageSliderDto = sliderService.updateImage(username, id, file, caption);
        return modelMapper.map(imageSliderDto, ImageSliderRest.class);
    }
}
