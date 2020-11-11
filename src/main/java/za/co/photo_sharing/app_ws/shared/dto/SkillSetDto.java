package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SkillSetDto {
    private long id;
    private String skill;
    private double rating;
    private double ratingCalc;
}
