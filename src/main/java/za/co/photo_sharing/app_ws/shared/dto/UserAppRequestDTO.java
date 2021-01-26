package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAppRequestDTO {


    private String firstName;
    private String lastName;
    private String email;
    private String webType;
    private String description;
    private String OrganizationUsername;
    private LocalDateTime requestDate;
    private String emailVerificationToken;
    private Boolean emailVerificationStatus = false;
    private Long appTypeKey;
    private AppTokenDTO appToken;
}
