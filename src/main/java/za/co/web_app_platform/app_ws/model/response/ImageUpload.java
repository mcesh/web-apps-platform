package za.co.web_app_platform.app_ws.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUpload {

    private String fileName;
    private String base64Image;
}
