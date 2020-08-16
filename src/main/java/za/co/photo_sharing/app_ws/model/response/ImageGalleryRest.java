package za.co.photo_sharing.app_ws.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageGalleryRest {

    private String caption;
    private String imageUrl;
    private Long userId;
    private CategoryRest category;
}
