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
import za.co.photo_sharing.app_ws.entity.SkillSet;
import za.co.photo_sharing.app_ws.model.request.AboutDetailsRequestModel;
import za.co.photo_sharing.app_ws.model.request.SkillSetRequestModel;
import za.co.photo_sharing.app_ws.model.response.AboutRest;
import za.co.photo_sharing.app_ws.model.response.SkillSetRest;
import za.co.photo_sharing.app_ws.services.AboutService;
import za.co.photo_sharing.app_ws.services.SkillSetService;
import za.co.photo_sharing.app_ws.shared.dto.AboutDTO;
import za.co.photo_sharing.app_ws.shared.dto.SkillSetDto;

import java.time.LocalDateTime;

@RestController
@RequestMapping("about") // http://localhost:8080/about/web-apps-platform
public class AboutResource {

    @Autowired
    private AboutService aboutService;
    @Autowired
    private SkillSetService setService;

    private static Logger LOGGER = LoggerFactory.getLogger(AboutResource.class);
    private ModelMapper modelMapper = new ModelMapper();
    public static Logger getLog() {
        return LOGGER;
    }

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

    @ApiOperation(value = "Get About Page Details Endpoint",
            notes = "${userResource.GetAboutPageDetails.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public AboutRest getAboutPageDetails(@PathVariable("email") String email){
        AboutDTO aboutDTO = aboutService.findByEmail(email);
        AboutRest aboutRest = modelMapper.map(aboutDTO, AboutRest.class);
        getLog().info("About Page Details: {} ", aboutRest);
        return aboutRest;
    }

    @ApiOperation(value = "Download About Page Image Endpoint",
            notes = "${userResource.AboutPageImage.ApiOperation.Notes}")
    @GetMapping(path = "download/about-page/{email}",
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public String downloadAboutPageImage(@PathVariable String email) {
        getLog().info("Getting About Page Image for {} ", email);
        String profileImage = aboutService.downloadAboutPageImage(email);
        getLog().info("Image Downloaded Successfully at: {} ", LocalDateTime.now());
        return profileImage;
    }

    @ApiOperation(value = "Get Skill Set Details Endpoint",
            notes = "${userResource.SkillSetDetails.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "byId/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SkillSetRest getSkillSet(@PathVariable("id") Long id){
        SkillSetDto skillSetDto = setService.findById(id);
        SkillSetRest skillSet = modelMapper.map(skillSetDto, SkillSetRest.class);
        getLog().info("SkillSet Details: {} ", skillSet);
        return skillSet;
    }

    @ApiOperation(value = "Update Skill Set Endpoint",
            notes = "${userResource.UpdateSkillSet.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PutMapping(path = "update/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SkillSetRest updateSkillSet(@PathVariable("id") Long id,
                                       @RequestBody SkillSetRequestModel skillSet){
        SkillSetDto setDto = modelMapper.map(skillSet, SkillSetDto.class);
        SkillSetDto skillSetDto = setService.updateSkillSet(id,setDto);
        SkillSetRest skillSetRest = modelMapper.map(skillSetDto, SkillSetRest.class);
        getLog().info("SkillSet Details: {} ", skillSetRest);
        return skillSetRest;
    }

}
