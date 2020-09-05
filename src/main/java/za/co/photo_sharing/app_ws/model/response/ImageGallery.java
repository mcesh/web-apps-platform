package za.co.photo_sharing.app_ws.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageGallery {

    private long id;
    private String caption;
    private CategoryRest category;
    private byte[] image;
}
