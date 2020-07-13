package za.co.photo_sharing.app_ws.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRest {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private Long cellNumber;
    private LocalDateTime registrationDate;
    private Set<UserRolesRest> userRoles;
    private boolean roleUpdated = false;
    private Set<AddressesRest> addresses;
    private CompanyRest company;
    private Boolean emailVerificationStatus;
    private String userProfileImageLink;
}
