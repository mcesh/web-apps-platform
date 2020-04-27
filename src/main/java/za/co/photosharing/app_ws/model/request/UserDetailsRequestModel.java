package za.co.photosharing.app_ws.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsRequestModel {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String username;
}
