package za.co.photo_sharing.app_ws.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.shared.dto.ArticleDTO;

public interface ArticleService extends UserDetailsService {

    ArticleDTO createPost(ArticleDTO articleDTO, UserProfile userProfile, MultipartFile file);
}
