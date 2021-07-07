package za.co.web_app_platform.app_ws.model.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAppReqRest {

    private String firstName;
    private String lastName;
    private String email;
    private String webType;
    private String description;
    private LocalDateTime requestDate;
    private boolean emailVerificationStatus;
    private Long appTypeKey;
}
