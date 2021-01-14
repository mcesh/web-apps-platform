package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageTypeDto {

    private long id;
    private Long key;
    private String code;
}
