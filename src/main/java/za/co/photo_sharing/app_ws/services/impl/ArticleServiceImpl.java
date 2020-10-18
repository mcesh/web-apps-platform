package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.constants.ArticleStatusTypeKeys;
import za.co.photo_sharing.app_ws.entity.*;
import za.co.photo_sharing.app_ws.exceptions.ArticleServiceException;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.model.response.ImageUpload;
import za.co.photo_sharing.app_ws.repo.ArticleRepository;
import za.co.photo_sharing.app_ws.services.*;
import za.co.photo_sharing.app_ws.shared.dto.ArticleDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
        UserProfile userProfile = modelMapper.map(userDto, UserProfile.class);
        ImageUpload imageUpload = utils.uploadImage(file, userProfile, ARTICLE_IMAGES);
        Category categoryNameResponse = getCategory(userDto, categoryName);
        Set<Tag> tags = getTags(articleDTO);

        articleStatus = statusService.findByStatus(status);
        Article article = modelMapper.map(articleDTO, Article.class);
        article.setEmail(userDto.getEmail());
        article.setBase64StringImage(imageUpload.getBase64Image());
        article.setStatus(articleStatus.getStatus());
        article.setTags(tags);
        article.setCategory(categoryNameResponse);

        if (isPublished()){
            article.initPublishDate();
        }
        Article savedArticle = articleRepository.save(article);
        ArticleDTO returnedArticle = modelMapper.map(savedArticle, ArticleDTO.class);

        mapTagsToString(savedArticle, returnedArticle);

        getLog().info("Persisted article.... {} ", returnedArticle);
        return returnedArticle;
    }

    @Override
    public ArticleDTO findById(Long id) {
        Optional<Article> article = articleRepository.findById(id);
        if (!article.isPresent()){

            throw new ArticleServiceException(HttpStatus.NOT_FOUND,ErrorMessages.ARTICLE_NOT_FOUND.getErrorMessage());
        }
        AtomicReference<ArticleDTO> articleDTO = new AtomicReference<>();
        article.map(article1 -> {
            articleDTO.set(modelMapper.map(article1, ArticleDTO.class));
            mapTagsToString(article1,articleDTO.get());
            return articleDTO;
        });
        getLog().info("Article found with ID {} ", articleDTO.get().getId());
        getLog().info("Article: {} ", articleDTO.get());
        return articleDTO.get();
    }

    @Override
    public List<ArticleDTO> findByEmail(String email,int page, int size) {
        Utils.validatePageNumberAndSize(page,size);
        List<ArticleDTO> articleDTOS = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles = articleRepository.findByEmail(email,pageable);
        List<Article> articleList = articles.getContent();
        if (CollectionUtils.isEmpty(articleList)){
            throw new ArticleServiceException(HttpStatus.NOT_FOUND,ErrorMessages.NO_ARTICES_FOUND_IN_RANGE.getErrorMessage());
        }
        getLog().info("Articles size found {} ", articleList.size());
        articleList.forEach(article -> {
            ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
            mapTagsToString(article,articleDTO);
            articleDTOS.add(articleDTO);
        });

        return articleDTOS;
    }

    private Category getCategory(UserDto userDto, String categoryName) {
        Category categoryNameResponse = categoryService.findByEmailAndCategoryName(userDto.getEmail(), categoryName);
        if (Objects.isNull(categoryNameResponse)){
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        int articleCount = categoryNameResponse.getArticleCount() + 1;
        categoryService.updateArticleCount(articleCount,categoryName,userDto.getEmail());
        categoryNameResponse.setArticleCount(articleCount);
        return categoryNameResponse;
    }

    private Set<Tag> getTags(ArticleDTO articleDTO) {
        Set<Tag> tags = new HashSet<>();
        if (articleDTO.getTags()!= null && articleDTO.getTags().size()> 0){
            tags = new HashSet<>(articleDTO.getTags().size());

            for (String tag: articleDTO.getTags()){
                Tag tagName = tagService.findOrCreateByName(tag);
                tags.add(tagName);
            }
        }
        return tags;
    }

    private void mapTagsToString(Article savedArticle, ArticleDTO returnedArticle) {
        Set<String> tagNames = new HashSet<>(savedArticle.getTags().size());

        for (Tag tag : savedArticle.getTags()) {
            tagNames.add(tag.getName());
        }
        returnedArticle.getTags().clear();
        returnedArticle.setTags(tagNames);
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
