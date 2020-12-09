package za.co.photo_sharing.app_ws.resource;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.config.SecurityConstants;
import za.co.photo_sharing.app_ws.model.request.ArticleDetailsRequestModel;
import za.co.photo_sharing.app_ws.model.response.ArticleRest;
import za.co.photo_sharing.app_ws.services.ArticleService;
import za.co.photo_sharing.app_ws.services.UserAppReqService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.ArticleDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserClientDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("article") // http://localhost:8080/article/web-apps-platform
@Slf4j
public class ArticleResource {

    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserAppReqService appReqService;
    private ModelMapper modelMapper = new ModelMapper();

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
        log.info("Creating article for {} current time is {} ", userDto.getEmail(), LocalDateTime.now());
        ArticleDTO article = articleService.createPost(articleDTO, userDto, file, category, status);
        return modelMapper.map(article, ArticleRest.class);
    }

    @ApiOperation(value = "Find Article By ID",
            notes = "${userAppRequestResource.ArticleByID.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ArticleRest getArticleById(Long id) {
        ArticleDTO articleDTO = articleService.findById(id);
        return modelMapper.map(articleDTO, ArticleRest.class);
    }

    @ApiOperation(value = "Find Articles By Email",
            notes = "${userAppRequestResource.ArticlesByEmail.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(value = "/by-email/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public List<ArticleRest> getArticlesByEmail(
            @RequestParam(value = "page", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_SIZE) Integer size,
            @PathVariable(name = "email") String email) {
        List<ArticleRest> articleRests = new ArrayList<>();
        log.info("Fetching Articles for {} current time is {} ", email, LocalDateTime.now());
        List<ArticleDTO> articleDTOList = articleService.findByEmail(email, page, size);
        articleDTOList.forEach(articleDTO -> {
            ArticleRest articleRest = modelMapper.map(articleDTO, ArticleRest.class);
            articleRests.add(articleRest);
        });
        log.info("Published articles: {} ", articleRests.size());
        return articleRests;
    }

    @ApiOperation(value = "Find Articles By Status",
            notes = "${userAppRequestResource.ArticlesByStatus.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(value = "/byStatus/{clientID}/{status}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public List<ArticleRest> getArticlesByStatus(
            @RequestParam(value = "page", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_SIZE) Integer size,
            @PathVariable(name = "status") String status,
            @PathVariable(name = "clientID") String clientID) {
        UserClientDTO clientDTO = appReqService.findByClientID(clientID);
        List<ArticleRest> articleRests = new ArrayList<>();
        log.info("Fetching {} articles for {} current time is {} ", status, clientDTO.getEmail(), LocalDateTime.now());
        List<ArticleDTO> articleDTOList = articleService.findArticlesByStatus(status, clientDTO.getEmail(), page, size);
        articleDTOList.forEach(articleDTO -> {
            ArticleRest articleRest = modelMapper.map(articleDTO, ArticleRest.class);
            articleRests.add(articleRest);
        });
        log.info("Articles found: {} ", articleRests.size());

        return articleRests;
    }

    @Secured("ROLE_ADMIN")
    @ApiOperation(value = "Update Article By ID",
            notes = "${userAppRequestResource.UpdateArticleByID.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PutMapping(value = "/{id}/{username}/{status}/{category}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ArticleRest updateArticleById(
            @PathVariable(name = "id") Long id,
            @PathVariable(name = "username") String username,
            @PathVariable(name = "status") String status,
            @PathVariable(name = "category") String category,
            @RequestBody ArticleDetailsRequestModel detailsRequestModel) {
        ArticleDTO articleDTO = modelMapper.map(detailsRequestModel, ArticleDTO.class);
        ArticleDTO updateById = articleService.updateById(id, username, articleDTO, category, status);
        ArticleRest articleRest = modelMapper.map(updateById, ArticleRest.class);
        log.info("Updated article with ID: {} ", articleRest.getId());
        return articleRest;
    }

    @Secured("ROLE_ADMIN")
    @ApiOperation(value = "Delete Article By Id Endpoint",
            notes = "${userResource.DeleteArticleById.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void deleteArticleById(@PathVariable("id") Long id) {
        log.info("Deleting Article with ID {} ", id);
        articleService.deleteArticleById(id);
    }

    @ApiOperation(value = "Find All Articles By Email",
            notes = "${userAppRequestResource.AllByArticlesByEmail.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(value = "/all/email/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public List<ArticleRest> getAllArticlesByEmail(
            @RequestParam(value = "page", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_SIZE) Integer size,
            @PathVariable(name = "email") String email) {
        List<ArticleRest> articleRests = new ArrayList<>();
        log.info("Fetching Articles for {} current time is {} ", email, LocalDateTime.now());
        List<ArticleDTO> articleDTOList = articleService.findAllArticlesByEmail(email, page, size);
        articleDTOList.forEach(articleDTO -> {
            ArticleRest articleRest = modelMapper.map(articleDTO, ArticleRest.class);
            articleRests.add(articleRest);
        });
        log.info("Articles found : {} ", articleRests.size());
        return articleRests;
    }

    @ApiOperation(value = "Find All Articles in the DB",
            notes = "${userAppRequestResource.FindAllArticles.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(value = "/all",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public List<ArticleRest> findAllArticles(
            @RequestParam(value = "page", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = SecurityConstants.DEFAULT_PAGE_SIZE) Integer size) {
        List<ArticleRest> articleRests = new ArrayList<>();
        log.info("Fetching Articles current time is {} ", LocalDateTime.now());
        List<ArticleDTO> articleDTOList = articleService.findAllArticles(page, size);
        articleDTOList.forEach(articleDTO -> {
            ArticleRest articleRest = modelMapper.map(articleDTO, ArticleRest.class);
            articleRests.add(articleRest);
        });
        log.info("List of Articles Found : {} ", articleRests.size());
        return articleRests;
    }


    @ApiOperation(value = "Like Article",
            notes = "${userAppRequestResource.LikeArticle.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(value = "/{id}/{username}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ArticleRest likeArticle(
            @PathVariable(name = "id") Long id,
            @PathVariable(name = "username") String username) {
        log.info(username + " likes this articles");
        ArticleDTO articleDTO = articleService.likeArticle(id, username);
        return modelMapper.map(articleDTO, ArticleRest.class);
    }

    @ApiOperation(value = "DisLike Article",
            notes = "${userAppRequestResource.DisLikeArticle.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @PostMapping(value = "dislike/{id}/{username}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ArticleRest dislikeArticle(
            @PathVariable(name = "id") Long id,
            @PathVariable(name = "username") String username) {
        log.info(username + " dislikes this articles");
        ArticleDTO articleDTO = articleService.dislikeArticle(id, username);
        return modelMapper.map(articleDTO, ArticleRest.class);
    }

    @ApiOperation(value = "Search Articles By Keyword",
            notes = "${userAppRequestResource.ArticlesByKeyword.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userResource.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(value = "/{title}/{email}",
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public List<ArticleRest> searchArticlesByKeyword(
            @PathVariable(name = "email") String email,
            @PathVariable(name = "title") String title) {
        List<ArticleRest> articleRests = new ArrayList<>();
        log.info("Fetching Articles for {} current time is {} ", email, LocalDateTime.now());
        List<ArticleDTO> articleDTOList = articleService.findByTitleContaining(title, email);
        articleDTOList.forEach(articleDTO -> {
            ArticleRest articleRest = modelMapper.map(articleDTO, ArticleRest.class);
            articleRests.add(articleRest);
        });
        log.info("Found Articles based on keyword : {} ", articleRests.size());
        return articleRests;
    }
}
