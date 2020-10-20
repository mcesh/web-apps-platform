package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.Article;
import za.co.photo_sharing.app_ws.entity.Comment;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.repo.ArticleRepository;
import za.co.photo_sharing.app_ws.repo.CommentRepository;
import za.co.photo_sharing.app_ws.services.ArticleService;
import za.co.photo_sharing.app_ws.services.CommentService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.ArticleDTO;
import za.co.photo_sharing.app_ws.shared.dto.CommentDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {

    private static Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentRepository commentRepository;
    private ModelMapper modelMapper = new ModelMapper();

    public static Logger getLog() {
        return LOGGER;
    }

    @Override
    public CommentDTO addComment(CommentDTO commentDTO, Long articleId, String username) {
        ArticleDTO articleDTO = articleService.findById(articleId);
        UserDto userDto = userService.findByUsername(username);
        Comment comment = modelMapper.map(commentDTO, Comment.class);
        comment.setEmail(articleDTO.getEmail());
        comment.setPostedDate(LocalDateTime.now());
        comment.setArticle(modelMapper.map(articleDTO, Article.class));
        comment.setUsername(userDto.getUsername());
        comment.setFirstName(userDto.getFirstName());
        comment.setLastName(userDto.getLastName());
        comment.setUserProfile(modelMapper.map(userDto, UserProfile.class));
        Comment savedComment = commentRepository.save(comment);
        CommentDTO dto = modelMapper.map(savedComment, CommentDTO.class);
        getLog().info("Persisted comment for {} ", username);
        return dto;
    }
}
