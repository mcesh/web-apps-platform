package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserClientDTO {

    private String email;
    private LocalDateTime creationTime;
    private String clientID;
}
