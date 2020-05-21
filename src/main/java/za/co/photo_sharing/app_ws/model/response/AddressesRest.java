package za.co.photo_sharing.app_ws.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressesRest {

    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;
    private Long userId;
}
