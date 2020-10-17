package za.co.photo_sharing.app_ws.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.shared.dto.ArticleDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

public interface ArticleService extends UserDetailsService {

    ArticleDTO createPost(ArticleDTO articleDTO, UserDto userDto,
                          MultipartFile file, String categoryName,
                          String articleStatus);
    ArticleDTO findById(Long id);
}
