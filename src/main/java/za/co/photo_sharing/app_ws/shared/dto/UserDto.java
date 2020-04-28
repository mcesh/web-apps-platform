package za.co.photo_sharing.app_ws.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto implements Serializable {

    private static final long serialVersionUID = 6835192601898364280L;
    private long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String username;
    private String encryptedPassword;
    private String emailVerificationToken;
    private Boolean emailVerificationStatus = false;
    private List<AddressDTO> addresses;



}
