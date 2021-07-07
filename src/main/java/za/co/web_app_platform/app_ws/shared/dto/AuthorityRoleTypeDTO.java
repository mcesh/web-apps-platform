package za.co.web_app_platform.app_ws.shared.dto;

import lombok.*;

import java.time.LocalDateTime;

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
