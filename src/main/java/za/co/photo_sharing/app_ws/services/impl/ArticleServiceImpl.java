package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.constants.ArticleStatusTypeKeys;
import za.co.photo_sharing.app_ws.constants.BucketName;
import za.co.photo_sharing.app_ws.entity.*;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.ArticleRepository;
import za.co.photo_sharing.app_ws.services.*;
import za.co.photo_sharing.app_ws.shared.dto.ArticleDTO;
import za.co.photo_sharing.app_ws.shared.dto.TagDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private Utils utils;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleStatusService statusService;

    private ModelMapper modelMapper = new ModelMapper();
    private static Logger LOGGER = LoggerFactory.getLogger(ArticleServiceImpl.class);
    public static final String ARTICLE_IMAGES = "ARTICLE_IMAGES";
    private static ArticleStatus articleStatus;

    @Override
    public ArticleDTO createPost(ArticleDTO articleDTO,UserDto userDto,
                                 MultipartFile file, String categoryName,
                                 String status) {

        utils.isImage(file);
        String base64Image = utils.uploadFile(file, userDto, ARTICLE_IMAGES);
        Category categoryNameResponse = getCategory(userDto, categoryName);
        Set<Tag> tags = new HashSet<>();
        if (articleDTO.getTags()!= null && articleDTO.getTags().size()> 0){
            tags = new HashSet<>(articleDTO.getTags().size());

            for (String tag: articleDTO.getTags()){
                Tag tagName = tagService.findOrCreateByName(tag);
                tags.add(tagName);
            }
        }
        
        articleStatus = statusService.findByStatus(status);
        Article article = modelMapper.map(articleDTO, Article.class);
        article.setEmail(userDto.getEmail());
        article.setBase64StringImage(base64Image);
        article.setStatus(articleStatus.getStatus());
        article.setTags(tags);
        article.setCategory(categoryNameResponse);

        if (isPublished()){
            article.initPublishDate();
        }
        Article savedArticle = articleRepository.save(article);
        ArticleDTO returnedArticle = modelMapper.map(savedArticle, ArticleDTO.class);

        Set<String> tagNames = new HashSet<>(savedArticle.getTags().size());

        for (Tag tag : savedArticle.getTags()) {
            tagNames.add(tag.getName());
        }
        returnedArticle.getTags().clear();
        returnedArticle.setTags(tagNames);

        getLog().info("Persisted article.... {} ", returnedArticle);
        return returnedArticle;
    }

    private Category getCategory(UserDto userDto, String categoryName) {
        Category categoryNameResponse = categoryService.findByEmailAndCategoryName(userDto.getEmail(), categoryName);
        if (Objects.isNull(categoryNameResponse)){
            throw new UserServiceException(ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        return categoryNameResponse;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }

    private boolean isPublished(){
        return articleStatus.getStatus().equalsIgnoreCase(ArticleStatusTypeKeys.PUBLISHED);
    }


    public static Logger getLog() {
        return LOGGER;
    }
}
