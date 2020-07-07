package za.co.photo_sharing.app_ws.model.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityRoleRest {

    private Long roleTypeKey;
    private LocalDateTime assignedOn;
    private LocalDateTime updatedOn;
}
