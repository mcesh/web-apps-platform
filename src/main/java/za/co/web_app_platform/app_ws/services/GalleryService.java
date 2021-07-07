package za.co.web_app_platform.app_ws.services;

import org.springframework.web.multipart.MultipartFile;
import za.co.web_app_platform.app_ws.model.response.ImageGallery;

public interface GalleryService {

    String uploadGallery(String email, MultipartFile file, String caption, String categoryName);
    ImageGallery getPhotoDetailsById(String email, Long id);
}
