package za.co.photo_sharing.app_ws.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.shared.dto.ArticleDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import java.util.List;

public interface ArticleService extends UserDetailsService {

    ArticleDTO createPost(ArticleDTO articleDTO, UserDto userDto,
                          MultipartFile file, String categoryName,
                          String articleStatus);
    ArticleDTO findById(Long id);
    List<ArticleDTO> findByEmail(String email,int page, int size);
    void deleteArticleById(Long id);
    List<ArticleDTO> findArticlesByStatus(String status, String email,int page, int size);
    ArticleDTO updateById(Long id, String username,ArticleDTO articleDTO, String category, String status);
    List<ArticleDTO> findAllArticlesByEmail(String email,int page, int size);
    List<ArticleDTO> findAllArticles(int page, int size);
}
