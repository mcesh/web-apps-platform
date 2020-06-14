package za.co.photo_sharing.app_ws.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequestModel {
    private String email;

}
