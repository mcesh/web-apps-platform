package za.co.web_app_platform.app_ws.model.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AboutRest {

    private long id;
    private String description;
    private Set<SkillSetRest> skillSets;
    private String base64StringImage;
}
