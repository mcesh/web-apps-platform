package za.co.web_app_platform.app_ws.services.impl;

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
import za.co.web_app_platform.app_ws.constants.ArticleStatusTypeKeys;
import za.co.web_app_platform.app_ws.entity.Article;
import za.co.web_app_platform.app_ws.entity.ArticleStatus;
import za.co.web_app_platform.app_ws.entity.Category;
import za.co.web_app_platform.app_ws.entity.Tag;
import za.co.web_app_platform.app_ws.exceptions.ArticleServiceException;
import za.co.web_app_platform.app_ws.exceptions.UserServiceException;
import za.co.web_app_platform.app_ws.model.response.ErrorMessages;
import za.co.web_app_platform.app_ws.repo.ArticleRepository;
import za.co.web_app_platform.app_ws.repo.CategoryRepository;
import za.co.web_app_platform.app_ws.services.*;
import za.co.web_app_platform.app_ws.shared.dto.ArticleDTO;
import za.co.web_app_platform.app_ws.shared.dto.UserDto;
import za.co.web_app_platform.app_ws.utility.Utils;

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
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public ArticleDTO createArticle(ArticleDTO articleDTO, UserDto userDto, String categoryName,
                                    String status) {

        Set<Tag> tags = getTags(articleDTO);

        articleStatus = statusService.findByStatus(status);
        Category categoryNameResponse = getCategory(userDto, categoryName, status);
        Article article = modelMapper.map(articleDTO, Article.class);
        article.setEmail(userDto.getEmail());
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

    @Override
    public void uploadArticleImage(MultipartFile file, long articleId) {
        if (!file.isEmpty()){
            Optional<Article> article = articleRepository.findById(articleId);
            if (!article.isPresent()){
                throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.ARTICLE_NOT_FOUND.getErrorMessage());
            }
            String imageUrl = fileUpload(file);
            article.map(article1 -> {
                article1.setImageUrl(imageUrl);
                articleRepository.save(article1);
                return true;
            });
        }
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
        if (!article.isPresent()){
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.ARTICLE_NOT_FOUND.getErrorMessage());
        }
        article.map(article1 -> {
            Category category = article1.getCategory();
            int articleCount = category.getArticleCount() - 1;
            category.setArticleCount(articleCount);
            if (!article1.getImageUrl().isEmpty()){
                deleteImage(article1);
            }
            log.info("Updating article count {} ", category.getArticleCount());
            article1.setStatus(statusService.findByStatus(ArticleStatusTypeKeys.DELETED).getStatus());
            categoryRepository.save(category);
            articleRepository.saveAndFlush(article1);
            articleRepository.delete(article1);
            return true;
        });
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
        AtomicReference<ArticleDTO> dto = new AtomicReference<>(new ArticleDTO());
        Optional<Article> article = getArticle(id);
        if (!article.isPresent()){
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.ARTICLE_NOT_FOUND.getErrorMessage());
        }
        article.map(article1 -> {
            if (!article1.getCategory().getName().equalsIgnoreCase(category)){
                if (article1.getCategory().getArticleCount() > 0){
                    article1.getCategory().setArticleCount(article1.getCategory().getArticleCount() - 1);
                    categoryRepository.save(article1.getCategory());
                }
                Category categoryName = categoryService.findByEmailAndCategoryName(userDto.getEmail(), category);
                article1.setCategory(categoryName);
                int articleCount = categoryName.getArticleCount() + 1;
                categoryName.setArticleCount(articleCount);
                categoryRepository.save(categoryName);
            }
            if (!article1.getStatus().equalsIgnoreCase(status)){
                ArticleStatus articleStatus = statusService.findByStatus(status);
                article1.setStatus(articleStatus.getStatus());
            }
            Set<Tag> tags = getTags(articleDTO);
            article1.setTitle(articleDTO.getTitle());
            article1.setTags(tags);
            article1.setCaption(articleDTO.getCaption());
            Article updatedArticle = articleRepository.save(article1);
            dto.set(modelMapper.map(updatedArticle, ArticleDTO.class));
            mapTagsToString(updatedArticle, dto.get());
            return true;
        });
        return dto.get();
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
        articleList.stream()
                .filter(article -> article.getStatus().equalsIgnoreCase(ArticleStatusTypeKeys.PUBLISHED))
                .forEach(article -> {
            ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
            mapTagsToString(article, articleDTO);
            articleDTO.setTotalPages(articles.getTotalElements());
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
        if (!article.isPresent()){
            throw new ArticleServiceException(HttpStatus.NOT_FOUND,ErrorMessages.ARTICLE_NOT_FOUND.getErrorMessage());
        }
        article.map(article1 -> {
            String uploadUrl = fileUpload(file);
            article1.setImageUrl(uploadUrl);
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
        if (!article.isPresent()){
            throw new ArticleServiceException(HttpStatus.NOT_FOUND,ErrorMessages.ARTICLE_NOT_FOUND.getErrorMessage());
        }
        article.map(article1 -> {
            deleteImage(article1);
            return true;
        });
    }

    @Transactional
    @Override
    public List<ArticleDTO> famousArticles(String email) {

        List<ArticleDTO> articleDTOS = new ArrayList<>();
        List<Article> articles = articleRepository.getFamousArticles(email);
        if (CollectionUtils.isEmpty(articles)) {
            return articleDTOS;
        }
        articles.stream()
                .filter(article -> article.getStatus().equalsIgnoreCase(ArticleStatusTypeKeys.PUBLISHED))
                .sorted(Comparator.comparing(Article::getPostedDate).reversed())
                .limit(7)
                .forEach(article -> {
                    ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
                    mapTagsToString(article, articleDTO);
                    articleDTOS.add(articleDTO);
                });

        return articleDTOS;
    }

    @Transactional
    @Override
    public List<ArticleDTO> findAllArticlesByEmail(String email) {
        List<ArticleDTO> articleDTOS = new ArrayList<>();
        List<Article> articles = articleRepository.findAll();
        articles.stream().
                filter(article-> article.getEmail().equalsIgnoreCase(email))
                .filter(article-> article.getStatus().equalsIgnoreCase(ArticleStatusTypeKeys.PUBLISHED))
                .forEach(article->{
                    ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
                    mapTagsToString(article, articleDTO);
                    articleDTOS.add(articleDTO);
                });
        return articleDTOS;
    }

    @Transactional
    @Override
    public List<ArticleDTO> findArticlesByEmailAndStatus(String email, String status, int page, int size) {
        Utils.validatePageNumberAndSize(page, size);
        List<ArticleDTO> articleDTOS = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles = articleRepository.findByEmailAndStatus(email,status, pageable);
        List<Article> articleList = articles.getContent();
        if (CollectionUtils.isEmpty(articleList)) {
            return articleDTOS;
        }
        articleList
                .forEach(article -> {
                    ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
                    mapTagsToString(article, articleDTO);
                    articleDTO.setTotalPages(articles.getTotalElements());
                    articleDTOS.add(articleDTO);
                });

        return articleDTOS;
    }

    @Transactional
    @Override
    public List<ArticleDTO> getArticlesByCategory(String email, String category, int page, int size) {
        Utils.validatePageNumberAndSize(page, size);
        List<ArticleDTO> articleDTOS = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articlePage = articleRepository.findByEmail(email, pageable);
        List<Article> articles = articlePage.getContent();
        if (CollectionUtils.isEmpty(articles)){
            return articleDTOS;
        }
        articles.stream()
                .filter(article -> article.getCategory().getName().equalsIgnoreCase(category))
                .forEach(article -> {
                    ArticleDTO articleDTO = modelMapper.map(article, ArticleDTO.class);
                    mapTagsToString(article, articleDTO);
                    articleDTOS.add(articleDTO);
                });
        return articleDTOS;
    }

    private void deleteImage(Article article1) {
        String base64StringImage = article1.getImageUrl();
        String[] split = base64StringImage.split("/");
        String publicId = split[7];
        String[] publicID = publicId.split(Pattern.quote("."));
        log.info("deleting image with publicID: {} ", publicID[0]);
        try {
            boolean deleteImage = utils.deleteImage(publicID[0]);
            if (deleteImage){
                article1.setImageUrl("");
                articleRepository.save(article1);
            }else {
                throw new UserServiceException(HttpStatus.INTERNAL_SERVER_ERROR,ErrorMessages.COULD_NOT_DELETE_RECORD.getErrorMessage());
            }
        }catch (IOException e){
            throw new RuntimeException("Error: {} " + e.getMessage());
        }
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

    private Category getCategory(UserDto userDto, String categoryName, String status) {
        Category categoryNameResponse = categoryService.findByEmailAndCategoryName(userDto.getEmail(), categoryName);
        if (Objects.isNull(categoryNameResponse)) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.CATEGORY_NOT_FOUND.getErrorMessage());
        }
        if (status.equalsIgnoreCase(ArticleStatusTypeKeys.PUBLISHED)){
            int articleCount = categoryNameResponse.getArticleCount() + 1;
            categoryService.updateArticleCount(articleCount, categoryName, userDto.getEmail());
            categoryNameResponse.setArticleCount(articleCount);
        }
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
