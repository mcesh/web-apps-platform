package za.co.web_app_platform.app_ws.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import za.co.web_app_platform.app_ws.shared.dto.ArticleDTO;
import za.co.web_app_platform.app_ws.shared.dto.UserDto;

import java.util.List;

public interface ArticleService extends UserDetailsService {

    ArticleDTO createArticle(ArticleDTO articleDTO, UserDto userDto, String categoryName,
                             String articleStatus);
    void uploadArticleImage(MultipartFile file, long articleId);
    ArticleDTO findById(Long id);
    List<ArticleDTO> findByEmail(String email,int page, int size);
    void deleteArticleById(Long id);
    List<ArticleDTO> findArticlesByStatus(String status, String email,int page, int size);
    ArticleDTO updateById(Long id, String username,ArticleDTO articleDTO, String category, String status);
    List<ArticleDTO> findAllArticlesByEmail(String email,int page, int size);
    List<ArticleDTO> findAllArticles(int page, int size);
    ArticleDTO likeArticle(Long postId, String username);
    ArticleDTO dislikeArticle(Long postId, String username);
    List<ArticleDTO> findByTitleContaining(String title, String email);
    ArticleDTO updateImage(Long articleID, String username, MultipartFile file);
    void deleteArticleImage(Long articleID, String username);
    List<ArticleDTO> famousArticles(String email);
}
