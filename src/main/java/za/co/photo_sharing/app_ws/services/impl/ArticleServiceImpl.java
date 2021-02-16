package za.co.photo_sharing.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.constants.ArticleStatusTypeKeys;
import za.co.photo_sharing.app_ws.entity.*;
import za.co.photo_sharing.app_ws.exceptions.ArticleServiceException;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.ArticleRepository;
import za.co.photo_sharing.app_ws.repo.CategoryRepository;
import za.co.photo_sharing.app_ws.services.*;
import za.co.photo_sharing.app_ws.shared.dto.ArticleDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    public static final String ARTICLE_IMAGES = "ARTICLE_IMAGES";
    private static ArticleStatus articleStatus;
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
    private CategoryRepository categoryRepository;
    @Autowired
    private ArticleStatusService statusService;
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public ArticleDTO createPost(ArticleDTO articleDTO, UserDto userDto,
                                 MultipartFile file, String categoryName,
                                 String status) {

        String imageUrl = fileUpload(file);
        Set<Tag> tags = getTags(articleDTO);

        articleStatus = statusService.findByStatus(status);
        Category categoryNameResponse = getCategory(userDto, categoryName);
        Article article = modelMapper.map(articleDTO, Article.class);
        article.setEmail(userDto.getEmail());
        article.setBase64StringImage(imageUrl);
        article.setStatus(articleStatus.getStatus());
        article.setTags(tags);
        article.setCategory(categoryNameResponse);

        if (isPublished()) {
            article.initPublishDate();
        }
        Article savedArticle = articleRepository.save(article);
        ArticleDTO returnedArticle = modelMapper.map(savedArticle, ArticleDTO.class);

        mapTagsToString(savedArticle, returnedArticle);

        log.info("Article Persisted Successfully at {} ", LocalDateTime.now());
        return returnedArticle;
    }

    @Transactional
    @Override
    public ArticleDTO findById(Long id) {
        Optional<Article> article = getArticle(id);
        AtomicReference<ArticleDTO> articleDTO = new AtomicReference<>();
        article.map(article1 -> {
            articleDTO.set(modelMapper.map(article1, ArticleDTO.class));
            mapTagsToString(article1, articleDTO.get());
            return articleDTO;
        });
        log.info("Article found with ID {} ", articleDTO.get().getId());
        return articleDTO.get();
    }

    @Transactional
    @Override
    public List<ArticleDTO> findByEmail(String email, int page, int size) {
        Utils.validatePageNumberAndSize(page, size);
        List<ArticleDTO> articleDTOS = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles = articleRepository.findByEmail(email, pageable);
        List<Article> articleList = articles.getContent();
        log.info("Articles found from DB {} ", articleList.size());
        if (CollectionUtils.isEmpty(articleList)) {
            return articleDTOS;
        }
        articleList
                .stream()
                .filter(article -> article.getStatus().equalsIgnoreCase(ArticleStatusTypeKeys.PUBLISHED))
                .sorted(Comparator.comparing(Article::getPostedDate).reversed())
                .forEach(article -> {
                    ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
                    mapTagsToString(article, articleDTO);
                    articleDTOS.add(articleDTO);
                });

        return articleDTOS;
    }

    @Transactional
    @Override
    public void deleteArticleById(Long id) {
        Optional<Article> article = getArticle(id);
        Category category = article.get().getCategory();
        int articleCount = category.getArticleCount() - 1;
        category.setArticleCount(articleCount);
        log.info("Updating article count {} ", category.getArticleCount());
        article.get().setStatus(statusService.findByStatus(ArticleStatusTypeKeys.DELETED).getStatus());
        categoryRepository.save(category);
        articleRepository.saveAndFlush(article.get());
        articleRepository.delete(article.get());
    }

    @Transactional
    @Override
    public List<ArticleDTO> findArticlesByStatus(String status, String email, int page, int size) {
        Utils.validatePageNumberAndSize(page, size);
        List<ArticleDTO> articleDTOS = new ArrayList<>();
        statusService.findByStatus(status);
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleRepository.findByStatus(status, pageable);
        List<Article> articles = articlePage.getContent();
        if (CollectionUtils.isEmpty(articles)) {
            return articleDTOS;
        }
        articles.stream()
                .filter(article -> article.getEmail().equalsIgnoreCase(email))
                .forEach(article -> {
                    ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
                    mapTagsToString(article, articleDTO);
                    articleDTOS.add(articleDTO);
                });
        return articleDTOS;
    }

    @Transactional
    @Override
    public ArticleDTO updateById(Long id, String username, ArticleDTO articleDTO, String category, String status) {
        UserDto userDto = userService.findByUsername(username);
        Optional<Article> article = getArticle(id);

        if (!article.get().getCategory().getName().equalsIgnoreCase(category)) {
            if (article.get().getCategory().getArticleCount() > 0) {
                article.get().getCategory().setArticleCount(article.get().getCategory().getArticleCount() - 1);
                categoryRepository.save(article.get().getCategory());
            }
            Category categoryName = categoryService.findByEmailAndCategoryName(userDto.getEmail(), category);
            article.get().setCategory(categoryName);
            int articleCount = categoryName.getArticleCount() + 1;
            categoryName.setArticleCount(articleCount);
            categoryRepository.save(categoryName);
        }

        if (!article.get().getStatus().equalsIgnoreCase(status)) {
            ArticleStatus articleStatus = statusService.findByStatus(status);
            article.get().setStatus(articleStatus.getStatus());
        }
        Set<Tag> tags = getTags(articleDTO);
        article.get().setTitle(articleDTO.getTitle());
        article.get().setTags(tags);
        article.get().setCaption(articleDTO.getCaption());
        Article updatedArticle = articleRepository.save(article.get());
        ArticleDTO dto = modelMapper.map(updatedArticle, ArticleDTO.class);
        mapTagsToString(updatedArticle, dto);
        return dto;
    }

    @Transactional
    @Override
    public List<ArticleDTO> findAllArticlesByEmail(String email, int page, int size) {
        Utils.validatePageNumberAndSize(page, size);
        List<ArticleDTO> articleDTOS = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles = articleRepository.findByEmail(email, pageable);
        List<Article> articleList = articles.getContent();
        if (CollectionUtils.isEmpty(articleList)) {
            return articleDTOS;
        }
        articleList.forEach(article -> {
            ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
            mapTagsToString(article, articleDTO);
            articleDTOS.add(articleDTO);
        });

        return articleDTOS;
    }

    @Transactional
    @Override
    public List<ArticleDTO> findAllArticles(int page, int size) {
        Utils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<ArticleDTO> articleDTOS = new ArrayList<>();
        Page<Article> articles = articleRepository.findAll(pageable);
        List<Article> articleList = articles.getContent();
        if (CollectionUtils.isEmpty(articleList)) {
            return articleDTOS;
        }
        articleList
                .stream()
                .sorted(Comparator.comparing(Article::getPostedDate).reversed())
                .forEach(article -> {
                    ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
                    mapTagsToString(article, articleDTO);
                    articleDTOS.add(articleDTO);
                });

        return articleDTOS;
    }

    @Transactional
    @Override
    public ArticleDTO likeArticle(Long postId, String username) {
        userService.findByUsername(username);
        Optional<Article> article1 = getArticle(postId);
        AtomicReference<ArticleDTO> articleDTO = new AtomicReference<>();
        article1.map(article -> {
            int articleLikes = article.getLikes() + 1;
            article.setLikes(articleLikes);
            Article savedArticle = articleRepository.save(article);
            articleDTO.set(modelMapper.map(savedArticle, ArticleDTO.class));
            mapTagsToString(savedArticle, articleDTO.get());
            return articleDTO.get();
        });
        return articleDTO.get();
    }

    @Transactional
    @Override
    public ArticleDTO dislikeArticle(Long postId, String username) {
        userService.findByUsername(username);
        Optional<Article> article1 = getArticle(postId);
        AtomicReference<ArticleDTO> articleDTO = new AtomicReference<>();
        article1.map(article -> {
            if (article.getLikes() > 0) {
                int articleLikes = article.getLikes() - 1;
                article.setLikes(articleLikes);
                Article savedArticle = articleRepository.save(article);
                articleDTO.set(modelMapper.map(savedArticle, ArticleDTO.class));
                mapTagsToString(savedArticle, articleDTO.get());
                return articleDTO.get();
            }
            articleDTO.set(modelMapper.map(article, ArticleDTO.class));
            mapTagsToString(article, articleDTO.get());
            return articleDTO.get();
        });
        return articleDTO.get();
    }

    @Transactional
    @Override
    public List<ArticleDTO> findByTitleContaining(String title, String email) {

        List<ArticleDTO> articleDTOS = new ArrayList<>();
        List<Article> articles = articleRepository.findByTitleContaining(title);
        if (CollectionUtils.isEmpty(articles)) {
            return articleDTOS;
        }
        articles
                .stream()
                .sorted(Comparator.comparing(Article::getPostedDate))
                .filter(article -> article.getEmail().equalsIgnoreCase(email))
                .filter(article -> article.getStatus().equalsIgnoreCase(ArticleStatusTypeKeys.PUBLISHED))
                .forEach(article -> {
                    ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
                    mapTagsToString(article, articleDTO);
                    articleDTOS.add(articleDTO);
                });
        return articleDTOS;
    }

    @Transactional
    @Override
    public ArticleDTO updateImage(Long articleID, String username, MultipartFile file) {
        AtomicReference<ArticleDTO> articleDTO = new AtomicReference<>(new ArticleDTO());
        userService.findByUsername(username);
        Optional<Article> article = articleRepository.findById(articleID);
        if (article.isEmpty()){
            throw new ArticleServiceException(HttpStatus.NOT_FOUND,ErrorMessages.ARTICLE_NOT_FOUND.getErrorMessage());
        }
        article.map(article1 -> {
            String uploadUrl = fileUpload(file);
            article1.setBase64StringImage(uploadUrl);
            articleRepository.save(article1);
            articleDTO.set(modelMapper.map(article1, ArticleDTO.class));
            return true;
        });

        return articleDTO.get();
    }

    @Override
    public void deleteArticleImage(Long articleID, String username) {
        userService.findByUsername(username);
        Optional<Article> article = articleRepository.findById(articleID);
        if (article.isEmpty()){
            throw new ArticleServiceException(HttpStatus.NOT_FOUND,ErrorMessages.ARTICLE_NOT_FOUND.getErrorMessage());
        }
        article.map(article1 -> {
            String base64StringImage = article1.getBase64StringImage();
            String[] split = base64StringImage.split("/");
            String publicId = split[7];
            String[] publicID = publicId.split(Pattern.quote("."));
            log.info("deleting image with publicID: {} ", publicID[0]);
            try {
                boolean deleteImage = utils.deleteImage(publicID[0]);
                if (deleteImage){
                    article1.setBase64StringImage("");
                    articleRepository.save(article1);
                }else {
                    throw new UserServiceException(HttpStatus.INTERNAL_SERVER_ERROR,ErrorMessages.COULD_NOT_DELETE_RECORD.getErrorMessage());
                }
            }catch (IOException e){
                throw new RuntimeException("Error: {} " + e.getMessage());
            }
            return true;
        });
    }

    public String fileUpload(MultipartFile file) {
        String cloudinaryUrl = "";
        if (file != null) {
            utils.isImage(file);
            try {
                cloudinaryUrl = utils.uploadToCloudinary(file);
            } catch (IOException e) {
                throw new ArticleServiceException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
            }
        }
        return cloudinaryUrl;
    }

    private Optional<Article> getArticle(Long postId) {
        Optional<Article> article = articleRepository.findById(postId);
        if (!article.isPresent()) {
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.ARTICLE_NOT_FOUND.getErrorMessage());
        }
        return article;
    }

    private Category getCategory(UserDto userDto, String categoryName) {
        Category categoryNameResponse = categoryService.findByEmailAndCategoryName(userDto.getEmail(), categoryName);
        if (Objects.isNull(categoryNameResponse)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        int articleCount = categoryNameResponse.getArticleCount() + 1;
        categoryService.updateArticleCount(articleCount, categoryName, userDto.getEmail());
        categoryNameResponse.setArticleCount(articleCount);
        return categoryNameResponse;
    }

    private Set<Tag> getTags(ArticleDTO articleDTO) {
        Set<Tag> tags = new HashSet<>();
        if (articleDTO.getTags() != null && articleDTO.getTags().size() > 0) {
            tags = new HashSet<>(articleDTO.getTags().size());

            for (String tag : articleDTO.getTags()) {
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

    private boolean isPublished() {
        return articleStatus.getStatus().equalsIgnoreCase(ArticleStatusTypeKeys.PUBLISHED);
    }
}
