package za.co.web_app_platform.app_ws.shared.dto;

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
