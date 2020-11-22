package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;
import za.co.photo_sharing.app_ws.entity.SkillSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AboutDTO {

    private String description;
    private Set<SkillSetDto> skillSets;
    private String base64StringImage;
}
