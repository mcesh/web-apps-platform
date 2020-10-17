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
import za.co.photo_sharing.app_ws.config.SecurityConstants;
import za.co.photo_sharing.app_ws.model.request.ArticleDetailsRequestModel;
import za.co.photo_sharing.app_ws.model.response.ArticleRest;
import za.co.photo_sharing.app_ws.services.ArticleService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.ArticleDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("article") // http://localhost:8080/article/web-apps-platform
public class ArticleResource {

    private static Logger LOGGER = LoggerFactory.getLogger(ArticleResource.class);
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    private ModelMapper modelMapper = new ModelMapper();

    public static Logger getLog() {
        return LOGGER;
    }

    @ApiOperation(value = "The Create Article Endpoint",
            notes = "${userResource.CreateArticle.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(path = "/addNew/{username}/{category}/{status}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ArticleRest createPost(@PathVariable("username") String username,
                                  @PathVariable("category") String category,
                                  @ModelAttribute ArticleDetailsRequestModel detailsRequestModel,
                                  @PathVariable("status") String status, MultipartFile file) {

        UserDto userDto = userService.findByUsername(username);
        ArticleDTO articleDTO = modelMapper.map(detailsRequestModel, ArticleDTO.class);
        getLog().info("Creating article for {} current time is {} ", userDto.getEmail(), LocalDateTime.now());
        ArticleDTO article = articleService.createPost(articleDTO, userDto, file, category, status);
        ArticleRest articleRest = modelMapper.map(article, ArticleRest.class);
        getLog().info("articleRest {} ", articleRest);
        return articleRest;
    }

    @ApiOperation(value="Find Article By ID",
            notes="${userAppRequestResource.ArticleByID.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @GetMapping(value = "/article/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ArticleRest getArticleById(Long id){
        ArticleDTO articleDTO = articleService.findById(id);
       return modelMapper.map(articleDTO,ArticleRest.class);
    }

    @ApiOperation(value="Find Articles By Email",
            notes="${userAppRequestResource.ArticlesByEmail.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="${userResource.authorizationHeader.description}", paramType="header")
    })
    @GetMapping(value = "/article/email/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public List<ArticleRest> getArticlesByEmail(
            @RequestParam(value = "page", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_SIZE) Integer size,
            @PathVariable(name = "email") String email){
        List<ArticleRest> articleRests = new ArrayList<>();
        getLog().info("Fetching Articles for {} current time is {} ", email, LocalDateTime.now());
        List<ArticleDTO> articleDTOList = articleService.findByEmail(email, page, size);
        articleDTOList.forEach(articleDTO -> {
            ArticleRest articleRest = modelMapper.map(articleDTO, ArticleRest.class);
            articleRests.add(articleRest);
        });

        return articleRests;
    }
}
