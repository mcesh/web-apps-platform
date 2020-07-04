package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppTokenDTO {

    private String tokenKey;
    private String primaryEmail;
    private UserAppRequestDTO userAppRequest;
}
