package za.co.photo_sharing.app_ws.model.response;

import lombok.*;
import za.co.photo_sharing.app_ws.constants.ArticlesStatus;
import za.co.photo_sharing.app_ws.shared.dto.CommentDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private String base64StringImage;
    private Set<CommentRest> commentList;
    private String status = ArticlesStatus.DRAFT.getText();
    private String email;
    private Set<String> tags = new HashSet<>();
}
