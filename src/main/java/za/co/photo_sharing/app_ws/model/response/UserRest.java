package za.co.photo_sharing.app_ws.model.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
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
    private AddressesRest address;
    private CompanyRest company;
    private Boolean emailVerificationStatus;
    private String userProfileImageLink;
    private Set<ImageGalleryRest> imageGallery;
}
