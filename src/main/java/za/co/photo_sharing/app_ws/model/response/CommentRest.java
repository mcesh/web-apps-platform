package za.co.photo_sharing.app_ws.model.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

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
    private UserRest userProfile;
}
