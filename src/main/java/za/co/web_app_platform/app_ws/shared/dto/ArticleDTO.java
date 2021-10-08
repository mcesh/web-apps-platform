package za.co.web_app_platform.app_ws.shared.dto;

import lombok.*;
import za.co.web_app_platform.app_ws.constants.ArticlesStatus;

import java.time.LocalDateTime;
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
    private Set<CommentDTO> commentList;
    private ArticlesStatus status = ArticlesStatus.DRAFT;
    private String email;
    private Set<String> tags;
    private CategoryDTO category;
    private String imageUrl;
    private long totalPages;
}
