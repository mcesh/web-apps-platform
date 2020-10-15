package za.co.photo_sharing.app_ws.model.request;

import lombok.*;
import za.co.photo_sharing.app_ws.constants.ArticlesStatus;
import za.co.photo_sharing.app_ws.shared.dto.CommentDTO;

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
public class ArticleDetailsRequestModel {
    private String title;
    private String caption;
    private List<String> tags;
}
