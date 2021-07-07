package za.co.web_app_platform.app_ws.shared.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDTO {

    private Long id;
    private String comment;
    private LocalDateTime postedDate;
    private String username;
    private UserDto userProfile;
}
