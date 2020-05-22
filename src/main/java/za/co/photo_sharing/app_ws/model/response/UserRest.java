package za.co.photo_sharing.app_ws.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<AddressesRest> addresses;
    private CompanyRest company;
}
