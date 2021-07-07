package za.co.web_app_platform.app_ws.services;

import za.co.web_app_platform.app_ws.shared.dto.CommentDTO;

public interface CommentService {

    CommentDTO addComment(CommentDTO commentDTO, Long articleId, String username);
    CommentDTO updateComment(CommentDTO commentDTO, Long commentId);
    CommentDTO getCommentById(Long id);
    void deleteCommentById(Long id);
}
