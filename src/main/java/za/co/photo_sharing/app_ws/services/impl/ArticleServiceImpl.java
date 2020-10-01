package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.entity.Article;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.services.ArticleService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.ArticleDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Objects;

public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private UserService userService;
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public ArticleDTO createPost(ArticleDTO articleDTO,UserProfile userProfile, MultipartFile file) {
        UserDto userDto = userService.findByEmail(userProfile.getEmail());
        if (Objects.isNull(userDto)){
            throw new UserServiceException(ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        }
        Article article = modelMapper.map(articleDTO, Article.class);
        article.setEmail(userDto.getEmail());
        article.setPostedDate(LocalDateTime.now());


        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }
}
