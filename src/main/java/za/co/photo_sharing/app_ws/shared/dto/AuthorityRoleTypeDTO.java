package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorityRoleTypeDTO {
    private long roleTypeKey;
    private UserDto userDetails;
}
