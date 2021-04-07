package za.co.photo_sharing.app_ws.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAppRequestModel {

    private String firstName;
    private String lastName;
    private String email;
    private String webType;
    private String description;
    private String OrganizationUsername;
}
