package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto implements Serializable {

    private static final long serialVersionUID = 6835192601898364280L;
    private long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private Long cellNumber;
    private String email;
    private String password;
    private String username;
    private String encryptedPassword;
    private String emailVerificationToken;
    private Boolean emailVerificationStatus = false;
    private List<AddressDTO> addresses;
    private CompanyDTO company;
    private LocalDateTime registrationDate;


}
