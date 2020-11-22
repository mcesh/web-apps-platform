package za.co.photo_sharing.app_ws.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SkillSetRest {
    private long id;
    private String skill;
    private double rating;
    private double ratingCalc;
}
