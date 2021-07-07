package za.co.web_app_platform.app_ws.model.request;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AboutDetailsRequestModel {

    private String description;
    private Set<SkillSetRequestModel> skillSets;
}
