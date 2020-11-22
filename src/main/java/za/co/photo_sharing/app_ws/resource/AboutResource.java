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
import za.co.photo_sharing.app_ws.config.SecurityConstants;
import za.co.photo_sharing.app_ws.model.request.AboutDetailsRequestModel;
import za.co.photo_sharing.app_ws.model.request.SkillSetRequestModel;
import za.co.photo_sharing.app_ws.model.response.AboutRest;
import za.co.photo_sharing.app_ws.model.response.SkillSetRest;
import za.co.photo_sharing.app_ws.services.AboutService;
import za.co.photo_sharing.app_ws.services.SkillSetService;
import za.co.photo_sharing.app_ws.shared.dto.AboutDTO;
import za.co.photo_sharing.app_ws.shared.dto.SkillSetDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("about") // http://localhost:8080/about/web-apps-platform
@Slf4j
public class AboutResource {

    @Autowired
    private AboutService aboutService;
    @Autowired
    private SkillSetService setService;
    private ModelMapper modelMapper = new ModelMapper();

    @ApiOperation(value = "Add About Page Endpoint",
            notes = "${userResource.AddAboutPage.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "/addNew/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public AboutRest addAboutPage(@PathVariable("email") String email,
                                  @RequestBody AboutDetailsRequestModel about) {
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
                                   @PathVariable("image") MultipartFile image) {
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
    public AboutRest getAboutPageDetails(@PathVariable("email") String email) {
        AboutDTO aboutDTO = aboutService.findByEmail(email);
        AboutRest aboutRest = modelMapper.map(aboutDTO, AboutRest.class);
        log.info("About Page Details: {} ", aboutRest);
        return aboutRest;
    }

    @ApiOperation(value = "Download About Page Image Endpoint",
            notes = "${userResource.AboutPageImage.ApiOperation.Notes}")
    @GetMapping(path = "download/about-page/{email}",
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public String downloadAboutPageImage(@PathVariable String email) {
        log.info("Getting About Page Image for {} ", email);
        String profileImage = aboutService.downloadAboutPageImage(email);
        log.info("Image Downloaded Successfully at: {} ", LocalDateTime.now());
        return profileImage;
    }

    @ApiOperation(value = "Get Skill Set Details Endpoint",
            notes = "${userResource.SkillSetDetails.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "byId/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SkillSetRest getSkillSet(@PathVariable("id") Long id) {
        SkillSetDto skillSetDto = setService.findById(id);
        SkillSetRest skillSet = modelMapper.map(skillSetDto, SkillSetRest.class);
        log.info("SkillSet Details: {} ", skillSet);
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
                                       @RequestBody SkillSetRequestModel skillSet) {
        SkillSetDto setDto = modelMapper.map(skillSet, SkillSetDto.class);
        SkillSetDto skillSetDto = setService.updateSkillSet(id, setDto);
        SkillSetRest skillSetRest = modelMapper.map(skillSetDto, SkillSetRest.class);
        log.info("SkillSet Details: {} ", skillSetRest);
        return skillSetRest;
    }

    @ApiOperation(value = "Delete Skill Set Endpoint",
            notes = "${userResource.DeleteSkillSet.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "delete/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public void deleteSkillSet(@PathVariable("id") Long id) {
        log.info("Deleting skill set with ID: {} ", id);
        setService.deleteSkillSetById(id);
        log.info("SkillSet successfully deleted at {} ", LocalDateTime.now());
    }

    @ApiOperation(value = "Get List of Skill Set Details Endpoint",
            notes = "${userResource.ListOfSkillSetDetails.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/list",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<SkillSetRest> getAllSkillSets(@RequestParam(value = "page", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                              @RequestParam(value = "size", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_SIZE) Integer size) {
        List<SkillSetRest> skillSetRests = new ArrayList<>();
        List<SkillSetDto> skillSetDto = setService.findAllSkillSets(page, size);
        skillSetDto.forEach(skillSetDto1 -> {
            SkillSetRest skillSet = modelMapper.map(skillSetDto1, SkillSetRest.class);
            skillSetRests.add(skillSet);
        });

        log.info("SkillSet Details Found: {} ", skillSetRests.size());
        return skillSetRests;
    }

    @ApiOperation(value = "Delete About Page Endpoint",
            notes = "${userResource.DeleteAboutPage.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "purge-page/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public void deleteAboutPageDetails(@PathVariable("id") Long id) {
        log.info("Deleting About Page with ID: {} ", id);
        aboutService.deleteAboutPageById(id);
        log.info("About Page successfully deleted at {} ", LocalDateTime.now());
    }

}
