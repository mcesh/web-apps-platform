package za.co.web_app_platform.app_ws.shared.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ImageBucketDto {

    private long id;
    private String caption;
    private String imageUrl;
    private String email;
    private String name;
}
