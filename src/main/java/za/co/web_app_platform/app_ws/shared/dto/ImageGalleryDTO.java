package za.co.web_app_platform.app_ws.shared.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageGalleryDTO {

    private Long id;
    private String caption;
    private Long userId;
    private String imageUrl;
    private UserDto userDetails;
    private CategoryDTO category;
}
