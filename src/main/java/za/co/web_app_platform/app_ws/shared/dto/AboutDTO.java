package za.co.web_app_platform.app_ws.shared.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AboutDTO {

    private long id;
    private String description;
    private Set<SkillSetDto> skillSets;
    private String base64StringImage;
}
