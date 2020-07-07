package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorityRoleTypeDTO {
    private long roleTypeKey;
    private UserDto userDetails;
    private LocalDateTime assignedOn;
    private LocalDateTime updatedOn;
}
