package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppTokenDTO {

    @Column(nullable = false, length = 50)
    private String appToken;

    @Column(length = 50, nullable = false)
    private String primaryEmail;
    private String secondaryEmail;
    private String thirdEmail;
    private String fourthEmail;
}
