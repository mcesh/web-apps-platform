package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.config.UserPrincipal;
import za.co.photo_sharing.app_ws.entity.Comment;
import za.co.photo_sharing.app_ws.shared.dto.CommentDTO;

public interface CommentService {

    CommentDTO addComment(CommentDTO commentDTO, Long articleId, String username);
    CommentDTO updateComment(CommentDTO commentDTO, Long commentId);
}
