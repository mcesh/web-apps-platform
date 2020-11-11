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
import za.co.photo_sharing.app_ws.model.request.AboutDetailsRequestModel;
import za.co.photo_sharing.app_ws.model.response.AboutRest;
import za.co.photo_sharing.app_ws.services.AboutService;
import za.co.photo_sharing.app_ws.shared.dto.AboutDTO;

@RestController
@RequestMapping("article") // http://localhost:8080/article/web-apps-platform
public class AboutResource {

    @Autowired
    private AboutService aboutService;

    private static Logger LOGGER = LoggerFactory.getLogger(AboutResource.class);
    private ModelMapper modelMapper = new ModelMapper();

    @ApiOperation(value = "Add About Page Endpoint",
            notes = "${userResource.AddAboutPage.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "/addNew/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public AboutRest addAboutPage(@PathVariable("email") String email,
                                  @RequestBody AboutDetailsRequestModel about){
        AboutDTO aboutDTO = modelMapper.map(about, AboutDTO.class);
        AboutDTO aboutPage = aboutService.addAboutPage(aboutDTO, email);
        return modelMapper.map(aboutPage, AboutRest.class);
    }

    @ApiOperation(value = "Add About Image Endpoint",
            notes = "${userResource.AddAboutImage.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "/image/{email}/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public AboutRest addAboutImage(@PathVariable("email") String email,
                                   @PathVariable("id") Long id,
                                   @PathVariable("image") MultipartFile image){
        AboutDTO aboutPage = aboutService.addImage(id, email, image);
        return modelMapper.map(aboutPage, AboutRest.class);
    }
}
