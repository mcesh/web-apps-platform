package za.co.photo_sharing.app_ws.services;

import org.springframework.web.multipart.MultipartFile;

public interface GalleryService {

    String uploadGallery(String email, MultipartFile file, String caption, String categoryName);
}
