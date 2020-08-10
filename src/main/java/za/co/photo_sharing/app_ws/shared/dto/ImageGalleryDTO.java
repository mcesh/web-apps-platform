package za.co.photo_sharing.app_ws.shared.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageGalleryDTO {

    private String caption;
    private Long userId;
    private String imageUrl;
    private UserDto userDetails;
}
