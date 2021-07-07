package za.co.web_app_platform.app_ws.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SkillSetRequestModel {

    private String skill;
    private double rating;
}
