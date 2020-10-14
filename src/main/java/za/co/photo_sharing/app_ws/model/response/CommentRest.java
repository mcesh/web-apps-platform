package za.co.photo_sharing.app_ws.model.response;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentRest {

    private Long id;
    private String content;
    private Date postedDate;
}
