package za.co.web_app_platform.app_ws.model.response;

import lombok.*;
import za.co.web_app_platform.app_ws.constants.ArticlesStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArticleRest {

    private long id;
    private String title;
    private String caption;
    private int likes;
    private LocalDateTime postedDate;
    private Set<CommentRest> commentList;
    private Set<String> tags = new HashSet<>();
    private String status = ArticlesStatus.DRAFT.getText();
    private CategoryRest category;
    private String email;
    private String imageUrl;
    private long totalPages;
}
