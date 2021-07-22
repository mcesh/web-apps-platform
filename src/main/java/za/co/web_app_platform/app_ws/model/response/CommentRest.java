package za.co.web_app_platform.app_ws.model.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentRest {

    private Long id;
    private String comment;
    private LocalDateTime postedDate;
    private String username;
}
