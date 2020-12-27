package za.co.photo_sharing.app_ws.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ImageSliderRest {

    private long id;
    private String caption;
    private String imageUrl;
    private String email;
}
