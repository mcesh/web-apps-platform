package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;
import za.co.photo_sharing.app_ws.constants.ArticlesStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArticleDTO {

    private long id;
    private String title;
    private String caption;
    private int likes;
    private LocalDateTime postedDate;
    private String base64StringImage;
    private Set<CommentDTO> commentList;
    private ArticlesStatus status = ArticlesStatus.DRAFT;
    private String email;
    private Set<String> tags;
}
