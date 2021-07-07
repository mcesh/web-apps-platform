package za.co.web_app_platform.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import za.co.web_app_platform.app_ws.entity.Article;
import za.co.web_app_platform.app_ws.entity.Comment;
import za.co.web_app_platform.app_ws.entity.UserProfile;
import za.co.web_app_platform.app_ws.exceptions.ArticleServiceException;
import za.co.web_app_platform.app_ws.model.response.ErrorMessages;
import za.co.web_app_platform.app_ws.repo.ArticleRepository;
import za.co.web_app_platform.app_ws.repo.CommentRepository;
import za.co.web_app_platform.app_ws.repo.UserRepo;
import za.co.web_app_platform.app_ws.services.ArticleService;
import za.co.web_app_platform.app_ws.services.CommentService;
import za.co.web_app_platform.app_ws.services.UserService;
import za.co.web_app_platform.app_ws.shared.dto.ArticleDTO;
import za.co.web_app_platform.app_ws.shared.dto.CommentDTO;
import za.co.web_app_platform.app_ws.shared.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepo userRepo;
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public CommentDTO addComment(CommentDTO commentDTO, Long articleId, String username) {
        UserProfile userProfile = userRepo.findByUsername(username);
        ArticleDTO articleDTO = articleService.findById(articleId);
        UserDto userDto = userService.findByUsername(username);
        Comment comment = modelMapper.map(commentDTO, Comment.class);
        comment.setEmail(articleDTO.getEmail());
        comment.setPostedDate(LocalDateTime.now());
        comment.setArticle(modelMapper.map(articleDTO, Article.class));
        comment.setUsername(userDto.getUsername());
        comment.setFirstName(userDto.getFirstName());
        comment.setLastName(userDto.getLastName());
        comment.setUserProfile(userProfile);
        Comment savedComment = commentRepository.save(comment);
        CommentDTO dto = modelMapper.map(savedComment, CommentDTO.class);
        log.info("Persisted comment for {} ", username);
        return dto;
    }

    @Override
    public CommentDTO updateComment(CommentDTO commentDTO, Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (!comment.isPresent()) {
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.COMMENT_NOT_FOUND.getErrorMessage());
        }
        comment.get().setComment(commentDTO.getComment());
        Comment updatedComment = commentRepository.save(comment.get());
        return modelMapper.map(updatedComment, CommentDTO.class);
    }

    @Override
    public CommentDTO getCommentById(Long id) {

        return null;
    }

    @Override
    public void deleteCommentById(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (!comment.isPresent()) {
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.COMMENT_NOT_FOUND.getErrorMessage());
        }
        commentRepository.delete(comment.get());
    }
}
